package org.matrix.framework.core.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

public class Get extends HTTPRequest {
    List<NameValuePair> parameters;

    public Get(String url) {
        super(url);
    }

    public void addParameter(String key, String value) {
        if (this.parameters == null)
            this.parameters = new ArrayList<NameValuePair>();
        this.parameters.add(new BasicNameValuePair(key, value));
    }

    public void addParameters(Map<String, String> params) {
        if (this.parameters == null)
            this.parameters = new ArrayList<NameValuePair>();
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                this.parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
    }

    @Override
    HttpRequestBase createRequest() {
        String completeURL = createURL();
        return new HttpGet(completeURL);
    }

    @Override
    protected void logRequestParams() {
        if (this.parameters != null)
            for (NameValuePair parameter : this.parameters)
                this.logger.debug("[param] " + parameter.getName() + "=" + parameter.getValue());
    }

    String createURL() {
        if (this.parameters != null) {
            String queryChar = this.url.contains("?") ? "&" : "?";
            return this.url + queryChar + URLEncodedUtils.format(this.parameters, "ISO-8859-1");
        }
        return this.url;
    }

    public String getParameterString() {
        if (null != this.parameters) {
            StringBuffer sb = new StringBuffer();
            for (NameValuePair nameValuePair : parameters) {
                sb.append(nameValuePair.getName());
                sb.append("=");
                sb.append(nameValuePair.getValue());
                sb.append(" ");
            }
            return sb.toString();
        }
        return null;
    }
}