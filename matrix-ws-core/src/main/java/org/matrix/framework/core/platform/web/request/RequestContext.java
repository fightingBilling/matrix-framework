package org.matrix.framework.core.platform.web.request;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.matrix.framework.core.platform.web.filter.RequestUtils;
import org.matrix.framework.core.util.ReadOnly;

public class RequestContext {
    private final ReadOnly<HttpServletRequest> request = new ReadOnly<HttpServletRequest>();

    private final ReadOnly<String> clientId = new ReadOnly<String>();

    private final ReadOnly<String> requestId = new ReadOnly<String>();

    private final ReadOnly<Date> requestDate = new ReadOnly<Date>();

    private final ReadOnly<String> action = new ReadOnly<String>();

    public String getRequestURLWithQueryString() {
        return RequestUtils.getRequestURLWithQueryString((HttpServletRequest) this.request.value());
    }

    public String getRelativeRequestURLWithQueryString() {
        return RequestUtils.getRelativeRequestURLWithQueryString((HttpServletRequest) this.request.value());
    }

    public String getClientRelativeRequestURLWithQueryString() {
        return RequestUtils.getClientRelativeRequestURLWithQueryString((HttpServletRequest) this.request.value());
    }

    public String getRelativeRequestURL() {
        return RequestUtils.getRelativeRequestURL((HttpServletRequest) this.request.value());
    }

    public boolean isSecure() {
        return ((HttpServletRequest) this.request.value()).isSecure();
    }

    public String getContextPath() {
        return ((HttpServletRequest) this.request.value()).getContextPath();
    }

    public RemoteAddress getRemoteAddress() {
        return RemoteAddress.create((HttpServletRequest) this.request.value());
    }

    public String getClientId() {
        return (String) this.clientId.value();
    }

    public void setClientId(String clientId) {
        this.clientId.set(clientId);
    }

    public String getRequestId() {
        return (String) this.requestId.value();
    }

    public void setRequestId(String requestId) {
        this.requestId.set(requestId);
    }

    public Date getRequestDate() {
        return (Date) this.requestDate.value();
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate.set(requestDate);
    }

    public String getAction() {
        return (String) this.action.value();
    }

    public void setAction(String action) {
        this.action.set(action);
    }

    public void setHTTPRequest(HttpServletRequest request) {
        this.request.set(request);
    }
}