package org.matrix.framework.core.zookeeper.support;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.zookeeper.ChildListener;
import org.matrix.framework.core.zookeeper.NodeListener;
import org.matrix.framework.core.zookeeper.StateListener;
import org.matrix.framework.core.zookeeper.ZookeeperClient;
import org.matrix.framework.core.zookeeper.setting.ZookeeperSettings;
import org.slf4j.Logger;

/**
 * 抽象ZookeeperClient
 * 
 * @author pankai 2015年9月30日
 */
public abstract class AbstractZookeeperClient<TargetChildListener> implements ZookeeperClient {

    private final Logger logger = LoggerFactory.getLogger(AbstractZookeeperClient.class);

    // 对应URL
    private final ZookeeperSettings zookeeperSettings;

    private final Set<StateListener> stateListeners = new CopyOnWriteArraySet<StateListener>();

    private final ConcurrentHashMap<String, ConcurrentHashMap<ChildListener, TargetChildListener>> childListeners = new ConcurrentHashMap<String, ConcurrentHashMap<ChildListener, TargetChildListener>>();

    private volatile boolean closed = false;

    public AbstractZookeeperClient(ZookeeperSettings zookeeperSettings) {
        this.zookeeperSettings = zookeeperSettings;
    }

    public ZookeeperSettings getZookeeperSettings() {
        return zookeeperSettings;
    }

    /**
     * 创建节点,支持递归创建.
     */
    @Override
    public void create(String path, boolean ephemeral) {
        int i = path.lastIndexOf('/');
        if (i > 0) {
            create(path.substring(0, i), false);
        }
        if (ephemeral) {
            createEphemeral(path);
        } else {
            createPersistent(path);
        }
    }

    /**
     * 增加状态监听器
     */
    public void addStateListener(StateListener listener) {
        stateListeners.add(listener);
    }

    /**
     * 移除状态监听器
     */
    public void removeStateListener(StateListener listener) {
        stateListeners.remove(listener);
    }

    /**
     * 获得状态监听器
     */
    public Set<StateListener> getSessionListeners() {
        return stateListeners;
    }

    @Override
    public List<String> addChildListener(String path, final ChildListener listener) {
        ConcurrentHashMap<ChildListener, TargetChildListener> listeners = childListeners.get(path);
        if (null == listeners) {
            childListeners.putIfAbsent(path, new ConcurrentHashMap<ChildListener, TargetChildListener>());
            listeners = childListeners.get(path);
        }
        TargetChildListener targetChildListener = listeners.get(listener);
        if (null == targetChildListener) {
            listeners.putIfAbsent(listener, createTargetChildListener(path, listener));
            targetChildListener = listeners.get(listener);
        }
        return addTargetChildListener(path, targetChildListener);
    }

    @Override
    public void removeChildListener(String path, ChildListener listener) {
        ConcurrentHashMap<ChildListener, TargetChildListener> listeners = childListeners.get(path);
        if (null != listeners) {
            TargetChildListener targetListener = listeners.remove(listener);
            if (null != targetListener) {
                removeTargetChildListener(path, targetListener);
            }
        }
    }

    /**
     * 状态改变,遍历所有监听器执行对应操作.
     */
    protected void stateChanged(int state) {
        for (StateListener sessionListener : getSessionListeners()) {
            sessionListener.stateChanged(state);
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            doClose();
            // catch Throwable,包括exception及error
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    // ================= 以下抽象方法需要不同的zookeeper client实现====================
    /**
     * 关闭zookeeper客户端的具体行为
     */
    protected abstract void doClose();

    /**
     * 创建没有数据的持久节点
     * 
     * @param path
     */
    protected abstract void createPersistent(String path);

    /**
     * 创建没有数据的临时节点
     * 
     * @param path
     */
    protected abstract void createEphemeral(String path);

    /**
     * 创建指定path下的listener(对应zookeeper概念中的事件监听器Watcher)
     * 
     * @param path 目标path
     * @param listener
     * @return 具体实现的watcher
     */
    protected abstract TargetChildListener createTargetChildListener(String path, ChildListener listener);

    /**
     * 将protected abstract TargetChildListener createTargetChildListener(String path, ChildListener listener)创建的TargetChildListener应用于zookeeper
     * 
     * @param path 目标path
     * @param listener
     * @return 应用成功的paths
     */
    protected abstract List<String> addTargetChildListener(String path, TargetChildListener listener);

    /**
     * 取消指定path的监听
     * 
     * @param path
     * @param listener
     */
    protected abstract void removeTargetChildListener(String path, TargetChildListener listener);

    // ================= 以上抽象方法需要不同的zookeeper client实现====================
}
