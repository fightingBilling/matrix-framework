package org.matrix.framework.core.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.springframework.util.StringUtils;

public class TextPost extends HTTPRequest {
    String body;
    String contentType = "text/plain";
    boolean chunked;

    public TextPost(String url) {
        super(url);
    }

    @Override
    HttpRequestBase createRequest() throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(this.url);
        if (StringUtils.hasText(this.body)) {
            AbstractHttpEntity entity = new StringEntity(this.body, "UTF-8");
            entity.setContentType(this.contentType + "; charset=" + "UTF-8");
            entity.setChunked(this.chunked);
            post.setEntity(entity);
        }
        return post;
    }

    protected void logRequestParams() {
        this.logger.debug("contentType=" + this.contentType);
        this.logger.debug("[param] body=" + this.body);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setChunked(boolean chunked) {
        this.chunked = chunked;
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isChunked() {
        return chunked;
    }

}