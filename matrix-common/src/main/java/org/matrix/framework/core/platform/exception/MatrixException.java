package org.matrix.framework.core.platform.exception;

@SuppressWarnings("serial")
public class MatrixException extends RuntimeException {
    public MatrixException(String message) {
        super(message);
    }

    public MatrixException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixException(Exception e) {
        super(e);
    }
}