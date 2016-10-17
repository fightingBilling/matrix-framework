package org.matrix.framework.core.platform.throttling;

public class RequestLimitException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 8399001086354408964L;

    public RequestLimitException() {
        super("HTTP请求超出设定的限制!");
    }

    public RequestLimitException(String message) {
        super(message);
    }
}
