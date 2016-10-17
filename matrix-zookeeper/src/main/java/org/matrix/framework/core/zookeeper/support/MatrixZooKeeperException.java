package org.matrix.framework.core.zookeeper.support;

@SuppressWarnings("serial")
public class MatrixZooKeeperException extends RuntimeException {
    public MatrixZooKeeperException(String message) {
        super(message);
    }

    public MatrixZooKeeperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixZooKeeperException(Exception e) {
        super(e);
    }
}
