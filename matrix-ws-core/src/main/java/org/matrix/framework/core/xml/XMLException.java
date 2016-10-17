package org.matrix.framework.core.xml;

public final class XMLException extends RuntimeException {

    private static final long serialVersionUID = -6466760357377450451L;

    public XMLException(String message) {
        super(message);
    }

    public XMLException(Throwable cause) {
        super(cause);
    }

}
