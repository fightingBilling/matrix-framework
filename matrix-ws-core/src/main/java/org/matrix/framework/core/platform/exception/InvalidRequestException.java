package org.matrix.framework.core.platform.exception;

@SuppressWarnings("serial")
public class InvalidRequestException extends RuntimeException {
    private String field;

    public InvalidRequestException(String message) {
        this(null, message, null);
    }

    public InvalidRequestException(String field, String message) {
        this(field, message, null);
    }

    public InvalidRequestException(String field, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
    }

    public String getField() {
        return this.field;
    }
}