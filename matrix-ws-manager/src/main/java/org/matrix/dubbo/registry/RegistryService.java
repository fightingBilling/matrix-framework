package org.matrix.dubbo.registry;

import java.util.List;

import org.matrix.dubbo.common.Server;

public interface RegistryService {

    /**
     * 注册服务
     * 
     * @param server
     */
    void register(Server server);

    /**
     * 注销服务
     * 
     * @param server
     */
    void unregister(Server server);

    /**
     * 订阅服务
     * 
     * @param url
     * @param listener
     */
    void subscribe(Server server, NotifyListener listener);

    /**
     * 取消订阅
     * 
     * @param server
     * @param listener
     */
    void unsubscribe(Server server, NotifyListener listener);

    /**
     * 查询符合条件的所有服务
     * 
     * @param server
     * @return
     */
    List<Server> lookup(Server server);

}
