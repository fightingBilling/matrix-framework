package org.matrix.framework.core.platform.cache.redis.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.matrix.framework.core.platform.cache.key.DefaultCacheKeyGenerator;
import org.matrix.framework.core.platform.cache.redis.prefix.DefaultCachePrefix;
import org.matrix.framework.core.util.TimeLength;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

/**
 * 使用redis作为缓存实现的配置类.
 * @author pankai
 * Oct 21, 2015
 */
public abstract class RedisCacheConfiguration implements CachingConfigurer {

    private RedisTemplate<String, Object> redisTemplate;

    //TODO 考虑与既有的redisTemplate分开.使用专门的cache server.
    @Inject
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        //可靠性及性能依赖于此.
        this.redisTemplate = redisTemplate;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Override
    public CacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        //不设置缓存名字,缓存将在运行时动态扩展.
        redisCacheManager.setCacheNames(null);
        //防止redis的key冲突.
        redisCacheManager.setUsePrefix(true);
        //方法执行成功之后才put/evict缓存.
        redisCacheManager.setTransactionAware(true);
        //不在启动时立即取远端的cache names.
        redisCacheManager.setLoadRemoteCachesOnStartup(false);
        long defaultExpiration = defaultExpiration();
        if (defaultExpiration < 0) {
            throw new IllegalArgumentException("Redis cache default expiration cannot less than 0!");
        }
        redisCacheManager.setDefaultExpiration(TimeLength.milliseconds(defaultExpiration).toSeconds());
        Map<String, Long> expires = expires();
        Map<String, Long> expirationDefinedForKeys = null;
        if (!CollectionUtils.isEmpty(expires)) {
            expirationDefinedForKeys = new HashMap<String, Long>();
            for (Entry<String, Long> entry : expires.entrySet()) {
                if (entry.getValue() < 0) {
                    throw new IllegalArgumentException("Redis cache expiration for key " + entry.getKey() + " cannot less than 0!");
                }
                expirationDefinedForKeys.put(entry.getKey(), TimeLength.milliseconds(defaultExpiration).toSeconds());
            }
        }
        redisCacheManager.setExpires(expirationDefinedForKeys);
        redisCacheManager.setCachePrefix(new DefaultCachePrefix(cachePrefix()));
        return redisCacheManager;
    }

    //Keep null
    @Override
    public CacheResolver cacheResolver() {
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

    /**
     * 可以返回null,表示不需要缓存错误处理.
     */
    protected abstract CacheErrorHandler cacheErrorHandler();

    /**
     * 默认的缓存过期时间.
     * 0表示不过期
     * 单位毫秒.
     */
    protected abstract long defaultExpiration();

    /**
     * 指定cache name的过期时间.
     * 可以返回null.
     * 单位毫秒.
     */
    protected abstract Map<String, Long> expires();

    /**
     * cache name前缀
     */
    protected abstract String cachePrefix();

}
