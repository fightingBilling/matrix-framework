package org.matrix.framework.core.platform.cache.injvm.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.matrix.framework.core.platform.SpringObjectFactory;
import org.matrix.framework.core.platform.cache.key.DefaultCacheKeyGenerator;
import org.matrix.framework.core.util.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * injvm cache default configuration.
 * 如果找不到缓存,会不使用缓存.
 * @warning for test env only as too many cache items may cause OOM?
 * @author pankai
 * Oct 22, 2015
 */
public abstract class InjvmCacheConfiguration implements CachingConfigurer {

    private SpringObjectFactory springObjectFactory;
    private Set<String> cacheNames = new HashSet<String>();
    private List<Cache> caches = new ArrayList<Cache>();

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Override
    public CacheManager cacheManager() {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(simpleCacheManager());
        compositeCacheManager.setFallbackToNoOpCache(true);
        return compositeCacheManager;
    }

    //TODO 考虑使用org.springframework.cache.concurrent.ConcurrentMapCacheManager代替.虽然实际上都没什么作用.
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SimpleCacheManager simpleCacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        addCaches(cacheNames);
        cacheNames.forEach(o -> {
            if (!StringUtils.hasText(o)) {
                throw new IllegalArgumentException("The cache name cannot be empty!");
            }
            springObjectFactory.registerSingletonBean(o, ConcurrentMapCacheFactoryBean.class);
            //ConcurrentMapCacheFactoryBean实现了getObjectType()方法,定义为ConcurrentMapCache类型.
                ConcurrentMapCache cache = springObjectFactory.getBean(o, ConcurrentMapCache.class);
                caches.add(cache);
            });
        manager.setCaches(caches);
        return manager;
    }

    @Override
    public CacheResolver cacheResolver() {
        //Keep null.
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

    @Inject
    public void setSpringObjectFactory(SpringObjectFactory springObjectFactory) {
        this.springObjectFactory = springObjectFactory;
    }

    /**
     * 缓存名称,不能重复且不能与已有bean的name冲突.
     */
    protected abstract void addCaches(Set<String> cacheNames);

    /**
     * 可以返回null,表示不需要缓存错误处理.
     */
    protected abstract CacheErrorHandler cacheErrorHandler();

}
