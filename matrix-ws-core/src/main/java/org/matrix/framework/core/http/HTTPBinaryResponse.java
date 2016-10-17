package org.matrix.framework.core.http;

public class HTTPBinaryResponse {
    final HTTPStatusCode statusCode;
    final HTTPHeaders headers;
    final byte[] responseContent;

    public HTTPBinaryResponse(HTTPStatusCode statusCode, HTTPHeaders headers, byte[] responseContent) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.responseContent = responseContent;
    }

    public byte[] getResponseContent() {
        return this.responseContent;
    }

    public HTTPStatusCode getStatusCode() {
        return this.statusCode;
    }

    public HTTPHeaders getHeaders() {
        return this.headers;
    }
}