package org.matrix.framework.core.platform;

import javax.inject.Inject;

import org.matrix.framework.core.database.memcached.MatrixMemcachedFactory;
import org.matrix.framework.core.platform.cache.CacheRegistry;
import org.matrix.framework.core.platform.cache.CacheRegistryImpl;
import org.matrix.framework.core.platform.cache.DefaultCacheKeyGenerator;
import org.matrix.framework.core.platform.cache.MemcachedCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;

/**
 * 应用memcached作为缓存的配置.
 * 
 * @author pankai 2015年6月1日
 */
public abstract class DefaultCacheConfig implements CachingConfigurer {

    @Inject
    MatrixMemcachedFactory matrixMemcachedFactory;

    @Bean
    public CacheManager cacheManager() {
        CacheManager cacheManager = createCacheManager();
        addCaches(new CacheRegistryImpl(cacheManager));
        return cacheManager;
    }

    private CacheManager createCacheManager() {
        MemcachedCacheManager cacheManager = new MemcachedCacheManager();
        cacheManager.setMatrixMemcachedFactory(this.matrixMemcachedFactory);
        return cacheManager;
    }

    @Override
    public CacheResolver cacheResolver() {
        // TODO 比cacheManager更加强力?待研究.
        return null;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new DefaultCacheKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        // TODO增加对缓存错误的处理.
        return null;
    }

    protected abstract void addCaches(CacheRegistry registry);

}
