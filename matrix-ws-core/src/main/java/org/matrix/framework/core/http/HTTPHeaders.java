package org.matrix.framework.core.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPHeaders {
    private final Logger logger = LoggerFactory.getLogger(HTTPHeaders.class);
    List<HTTPHeader> headers;

    static HTTPHeaders createResponseHeaders(HttpResponse response) {
        Header[] rawHeaders = response.getAllHeaders();
        List<HTTPHeader> httpHeaders = new ArrayList<HTTPHeader>(rawHeaders.length);
        for (Header header : rawHeaders) {
            httpHeaders.add(new HTTPHeader(header.getName(), header.getValue()));
        }

        HTTPHeaders headers = new HTTPHeaders();
        headers.headers = Collections.unmodifiableList(httpHeaders);
        return headers;
    }

    public void add(String name, String value) {
        if (this.headers == null)
            this.headers = new ArrayList<HTTPHeader>();
        this.headers.add(new HTTPHeader(name, value));
    }

    public List<HTTPHeader> getValues() {
        return this.headers;
    }

    public String getFirstHeaderValue(String name) {
        if (this.headers != null) {
            for (HTTPHeader header : this.headers) {
                if (header.getName().equals(name))
                    return header.getValue();
            }
        }
        return null;
    }

    void addHeadersToRequest(HttpRequestBase request) {
        if (this.headers != null)
            for (HTTPHeader header : this.headers)
                request.addHeader(header.getName(), header.getValue());
    }

    void log() {
        if (this.headers != null)
            for (HTTPHeader header : this.headers)
                this.logger.debug("[header] " + header.getName() + "=" + header.getValue());
    }

    public String getHeadsString() {
        if (null != headers) {
            StringBuffer sb = new StringBuffer();
            for (HTTPHeader httpHeader : headers) {
                sb.append(httpHeader.getName());
                sb.append("=");
                sb.append(httpHeader.getValue());
                sb.append(" ");
            }
        }
        return null;
    }
}