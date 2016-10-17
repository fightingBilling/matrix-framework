package org.matrix.framework.core.platform.web.request;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.matrix.framework.core.platform.ClassUtils;
import org.matrix.framework.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * 记录request相关信息到RequestContext
 * 
 * @author pankai 2015年6月16日
 */
public class RequestContextInterceptor extends HandlerInterceptorAdapter {
    public static final String HEADER_REQUEST_ID = "request-id";
    @SuppressWarnings("unused")
    private static final String PARAM_REQUEST_ID = "_requestId";
    private static final String ATTRIBUTE_CONTEXT_INITIALIZED = RequestContextInterceptor.class.getName()
            + ".CONTEXT_INITIALIZED";

    private final Logger logger = LoggerFactory.getLogger(RequestContextInterceptor.class);
    private RequestContext requestContext;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Boolean initialized = (Boolean) request.getAttribute(ATTRIBUTE_CONTEXT_INITIALIZED);
        if (!Boolean.TRUE.equals(initialized)) {
            this.requestContext.setHTTPRequest(request);
            assignRequestId(request);
            assignRequestDate();
            assignAction(handler);
            request.setAttribute(ATTRIBUTE_CONTEXT_INITIALIZED, Boolean.TRUE);
        }
        return true;
    }

    private void assignRequestDate() {
        Date now = new Date();
        this.requestContext.setRequestDate(now);
        this.logger.debug("requestDate={}", now);
    }

    private void assignAction(Object handler) {
        String action = null;
        if ((handler instanceof HandlerMethod))
            action = String.format("%s-%s", new Object[] {
                    ClassUtils.getSimpleOriginalClassName(((HandlerMethod) handler).getBean()),
                    ((HandlerMethod) handler).getMethod().getName() });
        else if ((handler instanceof ParameterizableViewController)) {
            action = getSimpleViewName(((ParameterizableViewController) handler).getViewName());
        }
        this.requestContext.setAction(action);
        MDC.put("MDC_ACTION", action);
        this.logger.debug("requestAction={}", action);
    }

    private void assignRequestId(HttpServletRequest request) {
        String requestId = getRequestId(request);
        RequestIdValidator.validateRequestId(requestId);
        this.requestContext.setRequestId(requestId);
        MDC.put("MDC_REQUEST_ID", requestId);
        this.logger.debug("requestId={}", requestId);
    }

    private String getRequestId(HttpServletRequest request) {
        String requestIdFromHeader = request.getHeader("request-id");
        if (StringUtils.hasText(requestIdFromHeader))
            return requestIdFromHeader;
        String requestIdFromParam = request.getParameter("_requestId");
        if (StringUtils.hasText(requestIdFromParam))
            return requestIdFromParam;
        this.logger.debug("request headers do not contain request-id, generate new one");
        return UUID.randomUUID().toString();
    }

    private String getSimpleViewName(String viewName) {
        int index = viewName.indexOf(':');
        if (index > -1)
            return viewName.substring(index + 1);
        return viewName;
    }

    @Inject
    public void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
}