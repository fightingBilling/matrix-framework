package org.matrix.framework.core.platform.cache.key;

/**
 * 当org.matrix.framework.core.platform.cache.key.DefaultCacheKeyGenerator不能满足需求的时候,使用该接口自定义cache key的生成.
 * @author pankai
 * Oct 22, 2015
 */
public abstract interface CacheKeyGenerator {

    public abstract String buildCacheKey();

}
