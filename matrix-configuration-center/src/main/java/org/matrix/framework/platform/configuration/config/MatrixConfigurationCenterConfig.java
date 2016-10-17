package org.matrix.framework.platform.configuration.config;

import javax.inject.Inject;

import org.matrix.framework.core.zookeeper.ZookeeperClient;
import org.matrix.framework.core.zookeeper.curator.CuratorZookeeperClient;
import org.matrix.framework.core.zookeeper.setting.ZookeeperSettings;
import org.matrix.framework.platform.configuration.center.ConfigurationCenter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * Matrix配置中心配置.继承此类,配置zookeeperSettings
 * @author pankai
 * Jan 12, 2016
 */
public abstract class MatrixConfigurationCenterConfig {

    private ZookeeperSettings zookeeperSettings;

    @Inject
    public void setZookeeperSettings(ZookeeperSettings zookeeperSettings) {
        this.zookeeperSettings = zookeeperSettings;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ZookeeperClient zookeeperClient() {
        return new CuratorZookeeperClient(zookeeperSettings);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ConfigurationCenter configurationCenter() {
        return new ConfigurationCenter();
    }

}
