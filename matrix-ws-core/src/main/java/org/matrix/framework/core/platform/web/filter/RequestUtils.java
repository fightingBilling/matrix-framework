package org.matrix.framework.core.platform.web.filter;

import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.util.StringUtils;

//From JSR-315
//9.4.2 Forwarded Request Parameters 
//
//Except for servlets obtained by using the getNamedDispatcher method, a servlet that has been invoked by another servlet using the forward method of RequestDispatcher has access to the path of the original request. 
//
//The following request attributes must be set: 
//
//javax.servlet.forward.request_uri 
//javax.servlet.forward.context_path 
//javax.servlet.forward.servlet_path 
//javax.servlet.forward.path_info 
//javax.servlet.forward.query_string 
//
//The values of these attributes must be equal to the return values of the HttpServletRequest methods getRequestURI, getContextPath, getServletPath, getPathInfo, getQueryString respectively, invoked on the request object passed to the first servlet object in the call chain that received the request from the client. These attributes are accessible from the forwarded servlet via the getAttribute method on the request object. Note that these attributes must always reflect the information in the original request even under the situation that multiple forwards and subsequent includes are called. 
//
//If the forwarded servlet was obtained by using the getNamedDispatcher method, these attributes must not be set. 
public class RequestUtils {

    public static String getRelativeRequestURLWithQueryString(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(getRelativeRequestURL(request));
        addQueryString(builder, request);
        return builder.toString();
    }

    public static String getRelativeRequestURL(HttpServletRequest request) {
        String forwardPath = (String) request.getAttribute("javax.servlet.forward.path_info");
        if (forwardPath != null)
            return forwardPath;
        return request.getPathInfo();
    }

    public static String getRequestURLWithQueryString(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(request.getRequestURL());
        addQueryString(builder, request);
        return builder.toString();
    }

    private static String getQueryString(HttpServletRequest request) {
        String forwardQueryString = (String) request.getAttribute("javax.servlet.forward.query_string");
        if (forwardQueryString != null)
            return forwardQueryString;
        return request.getQueryString();
    }

    private static void addQueryString(StringBuilder builder, HttpServletRequest request) {
        String queryString = getQueryString(request);
        if (StringUtils.hasText(queryString))
            builder.append('?').append(queryString);
    }

    public static String getClientRelativeRequestURLWithQueryString(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(getClientRelativeRequestURL(request));
        addQueryString(builder, request);
        return builder.toString();
    }

    public static String getClientRelativeRequestURL(HttpServletRequest request) {
        String forwardPath = (String) request.getAttribute("javax.servlet.forward.path_info");
        if (forwardPath != null)
            return forwardPath;
        forwardPath = request.getPathInfo();
        if (forwardPath != null)
            return forwardPath;
        if ("/".equals(request.getContextPath()))
            return request.getRequestURI();
        return request.getRequestURI().replace(request.getContextPath(), "");
    }

}
