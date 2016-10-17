package org.matrix.framework.core.zookeeper.curator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoChildrenForEphemeralsException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.matrix.framework.core.zookeeper.ChildListener;
import org.matrix.framework.core.zookeeper.NodeListener;
import org.matrix.framework.core.zookeeper.StateListener;
import org.matrix.framework.core.zookeeper.setting.ZookeeperSettings;
import org.matrix.framework.core.zookeeper.support.AbstractZookeeperClient;
import org.matrix.framework.core.zookeeper.support.MatrixZooKeeperException;

/**
 * 使用curator framework作为zookeeper client.
 * 
 * @author pankai 2015年9月29日
 */
public class CuratorZookeeperClient extends AbstractZookeeperClient<CuratorWatcher> {

    private ConcurrentHashMap<String, NodeCache> nodeCaches = new ConcurrentHashMap<String, NodeCache>();
    private ConcurrentHashMap<NodeListener, NodeCacheListener> nodeCacheListeners = new ConcurrentHashMap<NodeListener, NodeCacheListener>();

    private final CuratorFramework client;

    /**
     * 根据配置初始化CuratorZookeeperClient
     * @param zookeeperSettings
     */
    public CuratorZookeeperClient(ZookeeperSettings zookeeperSettings) {
        super(zookeeperSettings);
        Builder builder = CuratorFrameworkFactory.builder()//
                .connectString(zookeeperSettings.getConnectString())//
                .retryPolicy(new RetryNTimes(null == zookeeperSettings.getConnectRetryTimes() ? Integer.MAX_VALUE : zookeeperSettings.getConnectRetryTimes(), 1000))//
                .connectionTimeoutMs(3000);
        String authority = zookeeperSettings.getAuthority();
        if (StringUtils.isNotBlank(authority)) {
            builder = builder.authorization("digest", authority.getBytes());
        }
        client = builder.build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.LOST) {
                    // 当内部类必须要使用到外部类的this实例时,使用Class.this.然而可以使用lambda表达式就可以直接this了.可惜lambda省略了接口类型及参数类型,不方便我阅读代码.
                    CuratorZookeeperClient.this.stateChanged(StateListener.DISCONNECTED);
                } else if (newState == ConnectionState.CONNECTED) {
                    CuratorZookeeperClient.this.stateChanged(StateListener.CONNECTED);
                } else if (newState == ConnectionState.RECONNECTED) {
                    CuratorZookeeperClient.this.stateChanged(StateListener.RECONNECTED);
                } else if (newState == ConnectionState.SUSPENDED) {
                    CuratorZookeeperClient.this.stateChanged(StateListener.SUSPENDED);
                }
            }
        });
        client.start();
    }

    /**
     * 删除一个节点
     */
    @Override
    public void delete(String path) {
        try {
            client.delete().forPath(path);
        } catch (NoNodeException e) {
            //节点本就不存在.吃掉这个异常.
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    /**
     * 返回指定节点的子节点列表
     */
    @Override
    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    /**
     * 返回客户端是否已连接上服务端
     */
    @Override
    public boolean isConnected() {
        return client.getZookeeperClient().isConnected();
    }

    /**
     * 关闭客户端
     */
    @Override
    protected void doClose() {
        for (NodeCache nodeCache : nodeCaches.values()) {
            try {
                nodeCache.close();
            } catch (IOException e) {
                //考虑仅记录日志?
                throw new MatrixZooKeeperException(e.getMessage(), e);
            }
        }
        client.close();
    }

    /**
     * 创建持久节点
     */
    @Override
    protected void createPersistent(String path) {
        try {
            // curator默认创建的是持久节点
            client.create().forPath(path);
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    /**
     * 创建临时节点
     */
    @Override
    protected void createEphemeral(String path) {
        try {
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    private class CuratorWatchImpl implements CuratorWatcher {

        private volatile ChildListener listener;

        public CuratorWatchImpl(ChildListener listener) {
            this.listener = listener;
        }

        /**
         * 取消监听
         */
        public void unwatch() {
            this.listener = null;
        }

        /**
         * 在取消监听之前,这里会一直触发ChildListener接口的childChanged方法并反复注册Watcher进Zookeeper.
         */
        @Override
        public void process(WatchedEvent event) throws Exception {
            if (listener != null) {
                listener.childChanged(event.getPath(), client.getChildren().usingWatcher(this).forPath(event.getPath()));
            }
        }

    }

    @Override
    protected CuratorWatcher createTargetChildListener(String path, ChildListener listener) {
        return new CuratorWatchImpl(listener);
    }

    @Override
    protected List<String> addTargetChildListener(String path, CuratorWatcher listener) {
        try {
            return client.getChildren().usingWatcher(listener).forPath(path);
        } catch (NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    @Override
    protected void removeTargetChildListener(String path, CuratorWatcher listener) {
        ((CuratorWatchImpl) listener).unwatch();
    }

    /**
     * 返回实际使用的zookeeper client.
     * @return
     */
    public CuratorFramework getunderlyingClient() {
        return this.client;
    }

    @Override
    public void setData(String path, String data) {
        try {
            client.setData().forPath(path, data.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    @Override
    public String getData(String path) {
        try {
            return new String(client.getData().forPath(path), Charset.forName("UTF-8"));
            //如果指定节点不存在,返回空字符串.
        } catch (NoNodeException e) {
            return "";
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    @Override
    public void createWithParentsIfNeeded(String path, boolean ephemeral) {
        try {
            Stat stat = client.checkExists().forPath(path);
            if (stat != null) {
                //节点存在,且要求创建临时节点,但是目标节点不是临时节点
                if (ephemeral && (stat.getEphemeralOwner() == 0L)) {
                    throw new MatrixZooKeeperException("请求创建的节点" + path + "已经存在,期望创建临时节点,但其已经是持久节点.");
                }
                //节点存在,且要求创建持久节点,但是目标节点不是持久节点
                if (!ephemeral && (stat.getEphemeralOwner() != 0L)) {
                    throw new MatrixZooKeeperException("请求创建的节点" + path + "已经存在,期望创建持久节点,但其已经是临时节点.");
                }
            } else {
                //节点不存在,创建
                client.create().creatingParentsIfNeeded().withMode(ephemeral ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT).forPath(path);
            }
        } catch (NodeExistsException e) {
            throw new MatrixZooKeeperException("请求创建的节点" + path + "已经存在.");
        } catch (NoChildrenForEphemeralsException e) {
            throw new MatrixZooKeeperException("不能为非持久化节点创建子节点.Path:" + path);
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteIncludeChildren(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (NoNodeException e) {
            //节点本就不存在.吃掉这个异常.
        } catch (Exception e) {
            throw new MatrixZooKeeperException(e.getMessage(), e);
        }
    }

    /**
     * 增加节点监听
     */
    @Override
    public void addNodeListener(String path, NodeListener listener) {
        NodeCache nodeCache = nodeCaches.get(path);
        if (null == nodeCache) {
            nodeCache = new NodeCache(client, path, false);
            nodeCaches.putIfAbsent(path, nodeCache);
            try {
                nodeCache.start(true);
            } catch (Exception e) {
                throw new MatrixZooKeeperException(e.getMessage(), e);
            }
        }
        NodeCacheListener nodeCacheListener = nodeCacheListeners.get(listener);
        if (nodeCacheListener == null) {
            nodeCacheListener = new NodeCacheListener() {

                @Override
                public void nodeChanged() throws Exception {
                    listener.nodeChanged();
                }
            };
            nodeCacheListeners.putIfAbsent(listener, nodeCacheListener);
        }
        nodeCache.getListenable().addListener(nodeCacheListener);
    }

    /**
     * 移除节点监听
     */
    @Override
    public void removeNodeListener(String path, NodeListener listener) {
        NodeCache cache = nodeCaches.get(path);
        if (null == cache) {
            //no cache bind to the path, do nothing.
            return;
        }
        NodeCacheListener nodeCacheListener = nodeCacheListeners.get(listener);
        if (nodeCacheListener == null) {
            //no listener registered before, do nothing.
            return;
        }
        cache.getListenable().removeListener(nodeCacheListener);
    }

}
