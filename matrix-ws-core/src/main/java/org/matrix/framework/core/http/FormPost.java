package org.matrix.framework.core.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.CollectionUtils;

public class FormPost extends HTTPRequest {
    final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    boolean chunked;

    public FormPost(String url) {
        super(url);
    }

    public void addParameter(String key, String value) {
        this.parameters.add(new BasicNameValuePair(key, value));
    }

    public void setParameter(String key, String value) {
        for (Iterator<NameValuePair> iterator = this.parameters.listIterator(); iterator.hasNext();) {
            NameValuePair param = (NameValuePair) iterator.next();
            if (param.getName().equals(key))
                iterator.remove();
        }
        addParameter(key, value);
    }

    HttpRequestBase createRequest() throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(this.url);
        if (!CollectionUtils.isEmpty(this.parameters)) {
            AbstractHttpEntity entity = new UrlEncodedFormEntity(this.parameters, "UTF-8");
            entity.setChunked(this.chunked);
            post.setEntity(entity);
        }
        return post;
    }

    protected void logRequestParams() {
        for (NameValuePair parameter : this.parameters)
            this.logger.debug("[param] " + parameter.getName() + "=" + parameter.getValue());
    }

    public void setChunked(boolean chunked) {
        this.chunked = chunked;
    }
}