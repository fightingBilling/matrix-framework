package org.matrix.framework.core.http;

public class HTTPResponse {
    final HTTPStatusCode statusCode;
    final HTTPHeaders headers;
    final String responseText;

    public HTTPResponse(HTTPStatusCode statusCode, HTTPHeaders headers, String responseText) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.responseText = responseText;
    }

    public String getResponseText() {
        return this.responseText;
    }

    public HTTPStatusCode getStatusCode() {
        return this.statusCode;
    }

    public HTTPHeaders getHeaders() {
        return this.headers;
    }
}