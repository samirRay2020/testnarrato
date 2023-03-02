package org.rocketlane.narrato;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;

import java.net.URI;

public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "GET";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
