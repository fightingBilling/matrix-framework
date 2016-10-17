package org.matrix.dubbo.registry;

import java.util.List;

import org.matrix.dubbo.common.Server;

public interface NotifyListener {

    /**
     * 当收到服务变更时触发.
     * 
     * @param servers
     */
    void notify(List<Server> servers);

}
