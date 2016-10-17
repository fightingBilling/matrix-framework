package org.matrix.framework.core.http;

@SuppressWarnings("serial")
public class HTTPException extends RuntimeException {

    public HTTPException(String message) {
        super(message);
    }

    public HTTPException(Throwable cause) {
        super(cause);
    }
}