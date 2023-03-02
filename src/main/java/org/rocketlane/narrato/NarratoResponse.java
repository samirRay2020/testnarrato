package org.rocketlane.narrato;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NarratoResponse {

    public Links links;
    public Long currentPage;
    public Long total;
    public Long perPage;
    public Long totalPages;
    public List<Data> data;

    public static class Content{
        public Long id;
        public String name;
        public String type;
        public String data;
        public Object url;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Object getUrl() {
            return url;
        }

        public void setUrl(Object url) {
            this.url = url;
        }
    }

    public static class Data{
        @JsonProperty("task_id")
        public Long taskId;
        public String title;
        @JsonProperty("project_id")
        public Long projectId;
        @JsonProperty("folder_id")
        public Long folderId;
        @JsonProperty("folder_name")
        public String folderName;
        public String status;
        @JsonProperty("note_to_author")
        public String noteToAuthor;
        @JsonProperty("assignee_list")
        public ArrayList<Object> assigneeList;
        @JsonProperty("due_date")
        public Date dueDate;
        @JsonProperty("date_created")
        public Date dateCreated;
        @JsonProperty("date_modified")
        public Date dateModified;
        @JsonProperty("date_status_changed")
        public Date dateStatusChanged;
        public String template;
        @JsonProperty("word_count")
        public Object wordCount;
        public String keywords;
        public List<Content> contents;

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public Long getFolderId() {
            return folderId;
        }

        public void setFolderId(Long folderId) {
            this.folderId = folderId;
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNoteToAuthor() {
            return noteToAuthor;
        }

        public void setNoteToAuthor(String noteToAuthor) {
            this.noteToAuthor = noteToAuthor;
        }

        public ArrayList<Object> getAssigneeList() {
            return assigneeList;
        }

        public void setAssigneeList(ArrayList<Object> assigneeList) {
            this.assigneeList = assigneeList;
        }

        public Date getDueDate() {
            return dueDate;
        }

        public void setDueDate(Date dueDate) {
            this.dueDate = dueDate;
        }

        public Date getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(Date dateCreated) {
            this.dateCreated = dateCreated;
        }

        public Date getDateModified() {
            return dateModified;
        }

        public void setDateModified(Date dateModified) {
            this.dateModified = dateModified;
        }

        public Date getDateStatusChanged() {
            return dateStatusChanged;
        }

        public void setDateStatusChanged(Date dateStatusChanged) {
            this.dateStatusChanged = dateStatusChanged;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public Object getWordCount() {
            return wordCount;
        }

        public void setWordCount(Object wordCount) {
            this.wordCount = wordCount;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public List<Content> getContents() {
            return contents;
        }

        public void setContents(List<Content> contents) {
            this.contents = contents;
        }
    }

    public static class Links{
        public Object next;
        public Object previous;

        public Object getNext() {
            return next;
        }

        public void setNext(Object next) {
            this.next = next;
        }

        public Object getPrevious() {
            return previous;
        }

        public void setPrevious(Object previous) {
            this.previous = previous;
        }
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPerPage() {
        return perPage;
    }

    public void setPerPage(Long perPage) {
        this.perPage = perPage;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
