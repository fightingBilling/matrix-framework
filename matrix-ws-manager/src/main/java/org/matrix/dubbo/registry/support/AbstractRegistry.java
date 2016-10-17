package org.matrix.dubbo.registry.support;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.matrix.dubbo.common.Server;
import org.matrix.dubbo.common.utils.ConcurrentHashSet;
import org.matrix.dubbo.registry.NotifyListener;
import org.matrix.dubbo.registry.Registry;
import org.matrix.framework.core.log.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

public abstract class AbstractRegistry implements Registry {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Server registryServer;

    // 已注册的服务
    private final Set<Server> registered = new ConcurrentHashSet<Server>();

    // 已订阅服务与服务变更通知的映射
    private final Map<Server, Set<NotifyListener>> subscribed = new ConcurrentHashMap<Server, Set<NotifyListener>>();

    private final ConcurrentHashMap<Server, Map<String, List<Server>>> notified = new ConcurrentHashMap<Server, Map<String, List<Server>>>();

    public void setRegistryServer(Server server) {
        if (null == server) {
            throw new IllegalArgumentException("Cannot set null server!");
        }
        this.registryServer = server;
    }

    public Server getServer() {
        return registryServer;
    }

    public Set<Server> getRegistered() {
        return registered;
    }

    /**
     * 注册服务
     */
    @Override
    public void register(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("Cannot register null server!");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Register: " + server);
        }
        registered.add(server);
    }

    /**
     * 注销服务
     */
    @Override
    public void unregister(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("Cannot unregister null server!");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Unregister: " + server);
        }
        registered.remove(server);
    }

    /**
     * 订阅服务
     */
    @Override
    public void subscribe(Server server, NotifyListener listener) {
        if (server == null) {
            throw new IllegalArgumentException("Cannot subscribe null server!");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Cannot subscribe with null notifyListener!");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Subscribe: " + server);
        }
        // 先从已订阅映射获取变更通知列表
        Set<NotifyListener> listeners = subscribed.get(server);
        if (null == listeners) {
            // 如果还没有与该服务对应的映射关系,则新建
            subscribed.putIfAbsent(server, new ConcurrentHashSet<NotifyListener>());
            listeners = subscribed.get(server);
        }
        listeners.add(listener);
    }

    /**
     * 取消订阅服务
     */
    @Override
    public void unsubscribe(Server server, NotifyListener listener) {
        if (server == null) {
            throw new IllegalArgumentException("Cannot unsubscribe null server!");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Cannot unsubscribe with null notifyListener!");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Unsubscribe: " + server);
        }
        Set<NotifyListener> listeners = subscribed.get(server);
        if (null != listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * 查询符合条件的所有服务
     */
    @Override
    public List<Server> lookup(Server server) {

        return null;
    }

    /**
     * 订阅服务之后,通知指定的服务.
     * 
     * @TODO
     */
    protected void notify(Server server, NotifyListener listener, List<Server> servers) {
        if (server == null) {
            throw new IllegalArgumentException("Cannot notify null server!");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Cannot notify with null notifyListener!");
        }
        // TODO 考虑广播的情况.
        if (CollectionUtils.isEmpty(servers)) {
            // logger.warn("Ignore empty notify urls for subscribe server " + server);
            throw new IllegalArgumentException("Cannot notify empty servers!");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Notify servers for subscribe server " + server + ", servers: " + servers);
        }
        Map<String, List<Server>> categoryNotified = notified.get(server);
        if (null == categoryNotified) {
            notified.putIfAbsent(server, new ConcurrentHashMap<String, List<Server>>());
            categoryNotified = notified.get(server);
        }
        categoryNotified.put(server.getCategory(), servers);
        listener.notify(servers);
    }

    @Override
    public void destroy() {
        if (logger.isInfoEnabled()) {
            logger.info("Destroy registry:" + getServer());
        }
        Set<Server> destroyRegistered = new HashSet<Server>(getRegistered());

    }

}
