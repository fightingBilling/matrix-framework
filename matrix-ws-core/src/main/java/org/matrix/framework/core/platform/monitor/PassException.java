package org.matrix.framework.core.platform.monitor;

public class PassException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 3064353584705044877L;

    public PassException() {
        super("Forbidden!");
    }

    public PassException(String message) {
        super(message);
    }
}
