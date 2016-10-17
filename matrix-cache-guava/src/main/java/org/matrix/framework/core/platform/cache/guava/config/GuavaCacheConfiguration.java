package org.matrix.framework.core.platform.cache.guava.config;

import org.matrix.framework.core.platform.cache.guava.support.GuavaCacheStatsController;
import org.matrix.framework.core.platform.cache.key.DefaultCacheKeyGenerator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.google.common.cache.CacheBuilder;

/**
 * guava cache提供了三种回收方式.基于容量回收,定时回收和基于引用回收.此处选用了基于容量回收的方式.
 */
public abstract class GuavaCacheConfiguration implements CachingConfigurer {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GuavaCacheStatsController guavaCacheStatsController() {
        return new GuavaCacheStatsController();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Override
    public CacheManager cacheManager() {
        GuavaCacheManager manager = new GuavaCacheManager();
        manager.setCacheBuilder(getCacheBuilder());
        return manager;
    }

    @Override
    public CacheResolver cacheResolver() {
        //keep null.
        return null;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new DefaultCacheKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return cacheErrorHandler();
    }

    public CacheBuilder<Object, Object> getCacheBuilder() {
        return CacheBuilder.newBuilder().maximumSize(cacheMaximumSize()).recordStats();
    }

    /**
     * 可以返回null,表示不需要缓存错误处理.
     */
    protected abstract CacheErrorHandler cacheErrorHandler();

    /**
     * 缓存项的最大数量.
     */
    protected abstract long cacheMaximumSize();

}
