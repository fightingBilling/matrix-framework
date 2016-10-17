package org.matrix.framework.core.zookeeper;

/**
 * 状态变化接口
 * @author pankai
 * Jan 5, 2016
 */
public interface StateListener {

    int DISCONNECTED = 0;

    int CONNECTED = 1;

    int RECONNECTED = 2;

    int SUSPENDED = 3;

    void stateChanged(int connected);

}
