package org.matrix.framework.platform.configuration.config;

import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.matrix.framework.core.zookeeper.StateListener;
import org.matrix.framework.core.zookeeper.ZookeeperClient;
import org.matrix.framework.core.zookeeper.curator.CuratorZookeeperClient;
import org.matrix.framework.core.zookeeper.setting.ZookeeperSettings;
import org.matrix.framework.platform.configuration.client.ConfigurationClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * Matrix配置中心客户端配置.继承此类,配置zookeeperSettings
 * @author pankai
 * Jan 12, 2016
 */
public abstract class MatrixConfigurationClientConfig {

    private ZookeeperSettings zookeeperSettings;

    @Inject
    public void setZookeeperSettings(ZookeeperSettings zookeeperSettings) {
        this.zookeeperSettings = zookeeperSettings;
    }

    /**
     * spring创建这个bean的时候必须连接上zookeeper.
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ZookeeperClient zookeeperClient() {
        ZookeeperClient client = new CuratorZookeeperClient(zookeeperSettings);
        CountDownLatch latch = new CountDownLatch(1);
        client.addStateListener(new StateListener() {

            @Override
            public void stateChanged(int connected) {
                if (connected == StateListener.CONNECTED) {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
        return client;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ConfigurationClient configurationClient() {
        return new ConfigurationClient();
    }

}
