package org.matrix.framework.core.platform.concurrent;

public class TaskExecutionException extends RuntimeException {

    private static final long serialVersionUID = 123853813146188138L;

    public TaskExecutionException(Throwable cause) {
        super(cause);
    }
}
