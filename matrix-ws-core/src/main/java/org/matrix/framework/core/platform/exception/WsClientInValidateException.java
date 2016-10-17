package org.matrix.framework.core.platform.exception;

@SuppressWarnings("serial")
public class WsClientInValidateException extends RuntimeException {
    public WsClientInValidateException(String message) {
        super(message);
    }

    public WsClientInValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}