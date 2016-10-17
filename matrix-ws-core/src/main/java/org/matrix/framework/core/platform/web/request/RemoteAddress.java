package org.matrix.framework.core.platform.web.request;

import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.util.ReadOnly;
import org.matrix.framework.core.util.StringUtils;

public class RemoteAddress {
    private static final String HTTP_HEADER_X_FORWARDED_FOR = "x-forwarded-for";
    private final ReadOnly<String> remoteAddress = new ReadOnly<String>();

    private final ReadOnly<String> xForwardedFor = new ReadOnly<String>();

    public static RemoteAddress create(HttpServletRequest request) {
        String directRemoteAddress = request.getRemoteAddr();
        String xForwardedFor = request.getHeader("x-forwarded-for");
        RemoteAddress remoteAddress = new RemoteAddress();
        remoteAddress.setRemoteAddress(directRemoteAddress);
        remoteAddress.setXForwardedFor(xForwardedFor);
        return remoteAddress;
    }

    public String getRemoteAddress() {
        return (String) this.remoteAddress.value();
    }

    public String getXForwardedFor() {
        return (String) this.xForwardedFor.value();
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress.set(remoteAddress);
    }

    public void setXForwardedFor(String xForwardedFor) {
        this.xForwardedFor.set(xForwardedFor);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText((String) this.xForwardedFor.value())) {
            builder.append(this.xForwardedFor).append(", ");
        }
        builder.append(this.remoteAddress);
        return builder.toString();
    }
}