package org.matrix.framework.core.platform.web.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.log.TraceLogger;
import org.matrix.framework.core.util.StopWatch;
import org.slf4j.Logger;

/**
 * 记录日志
 * @author pankai
 * Jan 27, 2016
 */
public class MatrixFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(MatrixFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        StopWatch stopWatch = new StopWatch();
        HttpServletRequest originalRequest = null;
        RequestWrapper requestWrapper = null;
        try {
            this.logger.debug("===begin request processing===");
            TraceLogger.get().initialize();
            originalRequest = (HttpServletRequest) request;
            requestWrapper = new RequestWrapper(originalRequest);
            logRequest(requestWrapper, originalRequest);
            chain.doFilter(requestWrapper, response);
            this.logger.debug("=== end request processing ===");
        } finally {
            long elapsedTime = stopWatch.elapsedTime();
            if ((null != originalRequest) && (null != requestWrapper) && (!originalRequest.getRequestURI().contains("checkhealth"))) {
                LoggerFactory.MONITORLOGGER.getLogger().info("remoteAddress={}|requestURL={}|method={}|elapsedTime={}",
                        new Object[] { requestWrapper.getRemoteAddr(), requestWrapper.getRequestURL(), requestWrapper.getMethod(), Long.valueOf(elapsedTime) });
            }
            TraceLogger.get().clear();
        }
    }

    private void logRequest(RequestWrapper requestWrapper, HttpServletRequest originalRequest) throws IOException {
        this.logger.debug("requestURL={}", requestWrapper.getRequestURL());
        this.logger.debug("method={}", requestWrapper.getMethod());

        logHeaders(originalRequest);
        logParameters(originalRequest);
        this.logger.debug("remoteAddress={}", requestWrapper.getRemoteAddr());

        if (logBody(requestWrapper))
            this.logger.debug("body={}", requestWrapper.getOriginalBody());
    }

    private void logHeaders(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = (String) headers.nextElement();
            this.logger.debug("[header] {}={}", headerName, request.getHeader(headerName));
        }
    }

    private void logParameters(HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            this.logger.debug("[param] {}={}", paramName, request.getParameter(paramName));
        }
    }

    private boolean logBody(RequestWrapper request) {
        String method = request.getMethod().toUpperCase();
        return (("POST".equals(method)) || ("PUT".equals(method))) && (!request.isMultipart());
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
