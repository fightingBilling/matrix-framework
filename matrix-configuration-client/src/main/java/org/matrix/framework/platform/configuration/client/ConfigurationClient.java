package org.matrix.framework.platform.configuration.client;

import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.matrix.framework.core.zookeeper.NodeListener;
import org.matrix.framework.core.zookeeper.ZookeeperClient;
import org.matrix.framework.platform.configuration.context.ConfigurationContext;
import org.matrix.framework.platform.configuration.util.PathUtils;

/**
 * 配置中心客户端
 * @author pankai
 * Jan 12, 2016
 */
public class ConfigurationClient {

    private ZookeeperClient zookeeperClient;
    private ConcurrentHashMap<String, NodeListener> nodeListeners = new ConcurrentHashMap<String, NodeListener>();

    /**
     * 订阅配置
     */
    public void subscribe(ConfigurationContext context, ConfigurationCallback callback) {
        String path = PathUtils.getPath(context);
        NodeListener nodeListener = nodeListeners.get(path);
        if (null == nodeListener) {
            nodeListener = new NodeListener() {

                @Override
                public void nodeChanged() {
                    String newData = zookeeperClient.getData(path);
                    callback.onChange(newData);
                }
            };
            nodeListeners.putIfAbsent(path, nodeListener);
        } else {
            //移除旧的listener
            zookeeperClient.removeNodeListener(path, nodeListener);
            nodeListener = new NodeListener() {

                @Override
                public void nodeChanged() {
                    String newData = zookeeperClient.getData(path);
                    callback.onChange(newData);
                }
            };
            nodeListeners.putIfAbsent(path, nodeListener);
        }
        //加入新的.
        zookeeperClient.addNodeListener(path, nodeListener);
        //执行一次回调
        callback.onChange(zookeeperClient.getData(path));
    }

    /**
     * 取消订阅
     * @param context
     * @param listener
     */
    public void unsubscribe(ConfigurationContext context, ConfigurationCallback callback) {
        String path = PathUtils.getPath(context);
        NodeListener nodeListener = nodeListeners.get(path);
        if (null == nodeListener) {
            //do nothing.
        } else {
            zookeeperClient.removeNodeListener(path, nodeListener);
        }
    }

    @Inject
    public void setZookeeperClient(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }

}
