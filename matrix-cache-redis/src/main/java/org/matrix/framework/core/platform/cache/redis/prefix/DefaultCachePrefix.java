package org.matrix.framework.core.platform.cache.redis.prefix;

import org.matrix.framework.core.util.StringUtils;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class DefaultCachePrefix implements RedisCachePrefix {

    private final String prefix;
    private final RedisSerializer<String> serializer = new StringRedisSerializer();

    public DefaultCachePrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public byte[] prefix(String cacheName) {
        return serializer.serialize(StringUtils.hasText(cacheName) ? new StringBuilder(prefix).append(":").append(cacheName).append(":").toString() : cacheName.concat(":"));
    }

}
