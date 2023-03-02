package org.rocketlane.narrato;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class Main {

    private static final String GET_TASK_DATA_FROM_PROJECT = "https://narrato.io/api/v1/tasks/";
    private static final String UPDATE_TASK_DATA_IN_NARRATO = "https://narrato.io/api/v1/task/update-meta-data/";
    private static final String POST_DATA_TO_WEBFLOW = "https://api.webflow.com/collections/{collectionId}/items?live=false";
    private static final Long PROJECT_ID = 16548L;
    private static final String COLLECTION_ID = "63f76fe94e7bd001489655b2";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String NARRATO_TOKEN = "Token d7dbe9abafb27498f22afb8da5059d2204a6605e";
    private static final String WEBFLOW_TOKEN = "Bearer da219dfd0e9584d6b4ae13b1aa2cd96c5c4d2a27ada875ada04bd8b3a5b6f3e4";

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        // Get only status as "Publish Post"
        List<NarratoResponse> narratoResponseList = getNarratoResponse(PROJECT_ID);
        List<Long> narratoTaskIdsProcessed = new ArrayList<>();
        for (NarratoResponse narratoResponse : narratoResponseList) {
            List<NarratoResponse.Data> dataList = isNull(narratoResponse.getData()) ? new ArrayList<>() : narratoResponse.getData();
            for (NarratoResponse.Data data : dataList) {
                List<NarratoResponse.Content> contents = isNull(data.getContents()) ? new ArrayList<>() : data.getContents();

                JSONObject contentMapping = new JSONObject();

                if (!contentMapping.has("slug")) {
                    contentMapping.put("slug", isNull(data.getTitle()) ? "null" : getSlugName(data.getTitle()));
                }
                contentMapping.put("name", isNull(data.getTitle()) ? "null" : data.getTitle());
                contentMapping.put("_archived", false);
                contentMapping.put("_draft", false);

                for (NarratoResponse.Content content : contents) {
                    if ("attachment".equals(content.getType()) && content.getUrl() != null) {
                        contentMapping.put(getSlugName(content.getName()), Map.of(
                                "url", content.getUrl(),
                                "alt", "Image not found"
                        ));
                    }
                    else {
                        contentMapping.put(getSlugName(content.getName()), content.getData());
                    }
                }

                JSONObject fields = new JSONObject();
                fields.put("fields", contentMapping);

                Boolean isPostRequestSuccessful = postCollectionItemToWebFlow(COLLECTION_ID, fields.toString());
                // Adding 1 sec delay to avoid rate limiting in webflow
                Thread.sleep(1000);

                if (isPostRequestSuccessful) {
                    narratoTaskIdsProcessed.add(data.getTaskId());
                }
            }
        }

        // Marking narrato tasks as "Published"
        for (Long taskId : narratoTaskIdsProcessed) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("task_id", taskId);
            jsonObject.put("status", "Published");
            updateNarratoTaskToPublished(jsonObject.toString());
        }
    }

    public static List<NarratoResponse> getNarratoResponse(Long projectId) throws IOException, URISyntaxException {
        List<NarratoResponse> narratoResponses = new ArrayList<>();
        NarratoResponse narratoResponse = getNarratoResponse(projectId, 1L);
        if (narratoResponse != null) {
            narratoResponses.add(narratoResponse);
            if (narratoResponse.getTotalPages() != null && narratoResponse.getTotalPages() > 1) {
                Long pagesToExplore = narratoResponse.getTotalPages();
                for (int i = 2; i <= pagesToExplore; i++) {
                    NarratoResponse nextNarratoResponse = getNarratoResponse(projectId, Long.valueOf(i));
                    if (nextNarratoResponse != null) {
                        narratoResponses.add(nextNarratoResponse);
                    }
                }
            }
        }
        return narratoResponses;
    }

    public static NarratoResponse getNarratoResponse(Long projectId, Long page) throws URISyntaxException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGetWithEntity e = new HttpGetWithEntity();
        e.setURI(new URI(GET_TASK_DATA_FROM_PROJECT + "?project_id=" + projectId.toString() + "&page=" + page.toString()));
        e.setHeader("User-Agent", USER_AGENT);
        e.setHeader("Authorization", NARRATO_TOKEN);
        e.setHeader("Accept","application/json");

        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("status", "Publish Post"));
        e.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

        HttpResponse response = httpClient.execute(e);
        String jsonString = EntityUtils.toString(response.getEntity());
        NarratoResponse narratoResponse = null;
        if (jsonString.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            narratoResponse = objectMapper.readValue(jsonString, NarratoResponse.class);
            return narratoResponse;
        }
        return null;
    }

    public static Boolean postCollectionItemToWebFlow(String collectionId, String jsonRequestBody) throws IOException {
        String url = POST_DATA_TO_WEBFLOW.replace("{collectionId}", collectionId);
        final HttpPost httpPost = new HttpPost(url);

        final StringEntity entity = new StringEntity(jsonRequestBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("User-Agent", USER_AGENT);
        httpPost.setHeader("accept", "application/json");
        httpPost.setHeader("content-type", "application/json");
        httpPost.setHeader("authorization", WEBFLOW_TOKEN);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client
                     .execute(httpPost)) {

            String result = EntityUtils.toString(response.getEntity());
            return response.getStatusLine().getStatusCode() == 200;
        }
    }

    public static String getSlugName(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        return string.toLowerCase().replaceAll("\\s+", "-");
    }


    public static void updateNarratoTaskToPublished(String jsonRequestBody) throws IOException, URISyntaxException {
        final HttpPut httpPut = new HttpPut(UPDATE_TASK_DATA_IN_NARRATO);

        final StringEntity entity = new StringEntity(jsonRequestBody, ContentType.APPLICATION_JSON);
        httpPut.setEntity(entity);
        httpPut.setHeader("User-Agent", USER_AGENT);
        httpPut.setHeader("accept", "application/json");
        httpPut.setHeader("content-type", "application/json");
        httpPut.setHeader("authorization", NARRATO_TOKEN);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client
                     .execute(httpPut)) {

            String result = EntityUtils.toString(response.getEntity());
        }
    }
}