package org.matrix.framework.core.database.redis.config;

import org.matrix.framework.core.database.redis.monitor.RedisHealthController;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * 默认redis配置.
 * @author pankai
 * Nov 3, 2015
 */
public abstract class DefaultRedisConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public RedisHealthController redisHealthController() {
        return new RedisHealthController();
    }

}
