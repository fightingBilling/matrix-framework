package org.matrix.framework.core.http;

import java.io.IOException;

import org.apache.http.client.methods.HttpRequestBase;
import org.matrix.framework.core.log.LoggerFactory;
import org.slf4j.Logger;

public abstract class HTTPRequest {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String url;
    private final HTTPHeaders headers = new HTTPHeaders();

    protected HTTPRequest(String url) {
        this.url = url;
    }

    public void addHeader(String name, String value) {
        this.headers.add(name, value);
    }

    public void setAccept(String contentType) {
        this.headers.add("Accept", contentType);
    }

    public HttpRequestBase createHTTPRequest() throws IOException {
        HttpRequestBase request = createRequest();
        this.headers.addHeadersToRequest(request);
        return request;
    }

    public void logRequest() {
        this.logger.debug("====== http request begin ======");
        this.headers.log();
        logRequestParams();
        this.logger.debug("====== http request end ======");
    }

    abstract HttpRequestBase createRequest() throws IOException;

    protected abstract void logRequestParams();

    public String getUrl() {
        return url;
    }

    public HTTPHeaders getHeaders() {
        return headers;
    }

}