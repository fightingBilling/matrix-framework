package org.matrix.framework.core.platform.exception;

@SuppressWarnings("serial")
public class SessionTimeOutException extends RuntimeException {
    public SessionTimeOutException(String message) {
        super(message);
    }

    public SessionTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }
}