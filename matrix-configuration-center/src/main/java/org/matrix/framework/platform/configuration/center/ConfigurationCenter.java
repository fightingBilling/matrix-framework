package org.matrix.framework.platform.configuration.center;

import javax.inject.Inject;

import org.matrix.framework.core.zookeeper.ZookeeperClient;
import org.matrix.framework.platform.configuration.context.ConfigurationContext;
import org.matrix.framework.platform.configuration.util.PathUtils;

/**
 * 配置管理器
 * @author pankai
 * Jan 12, 2016
 */
public class ConfigurationCenter {

    private ZookeeperClient zookeeperClient;

    /**
     * 返回配置
     * @param context
     * @return
     */
    public String getConfigurableData(ConfigurationContext context) {
        String path = PathUtils.getPath(context);
        zookeeperClient.createWithParentsIfNeeded(path, false);
        return zookeeperClient.getData(path);
    }

    /**
     * 设置配置
     * @param context
     * @param data
     */
    public void setConfigurableData(ConfigurationContext context, String data) {
        zookeeperClient.setData(PathUtils.getPath(context), data);
    }

    @Inject
    public void setZookeeperClient(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }

}
