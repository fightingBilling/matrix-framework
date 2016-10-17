package org.matrix.framework.core.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UriComponentsBuilder;

public class URLInfo {

    private String path;
    private String scheme;
    private String method;
    private String host;
    private int port;
    private Map<String, String[]> parameters;

    public String getPath() {
        return path;
    }

    public void setPath(String url) {
        this.path = url;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * load URL from HttpServletRequest
     * 
     * @param request
     */
    public static URLInfo load(HttpServletRequest request) {
        URLInfo url = new URLInfo();
        url.setScheme(request.getScheme());
        url.setMethod(request.getMethod());
        url.setPath(request.getRequestURI());
        url.setHost(request.getServerName());
        url.setParameters(request.getParameterMap());
        url.setPort(request.getServerPort());
        return url;
    }

    @Override
    public String toString() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(scheme).host(host).port(port).path(path);
        for (Entry<String, String[]> parameter : parameters.entrySet())
            try {
                uriBuilder.queryParam(parameter.getKey(), encodeParameters(parameter.getValue()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        return uriBuilder.build().toUriString();
    }

    private Object[] encodeParameters(String[] parameters) throws UnsupportedEncodingException {
        Object[] encodedParameters = new Object[] { parameters.length };
        for (int i = 0; i < parameters.length; i++)
            encodedParameters[i] = URLEncoder.encode(parameters[i], "UTF-8");
        return encodedParameters;
    }
}
