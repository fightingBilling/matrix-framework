package org.matrix.dubbo.common;

public interface Node {

    Server getServer();

    boolean isAvailable();

    void destroy();

}
