package org.matrix.framework.core.platform.cache;

import org.matrix.framework.core.util.TimeLength;
import org.springframework.cache.CacheManager;

public class CacheRegistryImpl implements CacheRegistry {

    private final CacheManager cacheManager;

    public CacheRegistryImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void addCache(String groupName, String cacheName, TimeLength expirationTime) {
        ((MemcachedCacheManager) this.cacheManager).add(groupName, cacheName, expirationTime);
    }

}
