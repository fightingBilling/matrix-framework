package org.matrix.framework.core.platform.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.matrix.framework.core.database.memcached.MatrixMemcachedFactory;
import org.matrix.framework.core.util.AssertUtils;
import org.matrix.framework.core.util.TimeLength;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

//@Caching的使用值得学习
public class MemcachedCacheManager extends AbstractCacheManager {

    private final List<MemcachedCache> caches = new ArrayList<MemcachedCache>();
    private MatrixMemcachedFactory matrixMemcachedFactory;

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return this.caches;
    }

    public void add(String groupName, String cacheName, TimeLength expirationTime) {
        AssertUtils.assertNotNull(this.matrixMemcachedFactory, "matrixMemcachedFactory must be created before adding cache");
        this.caches.add(new MemcachedCache(cacheName, expirationTime, this.matrixMemcachedFactory.getClient(groupName), this.matrixMemcachedFactory.getServers(groupName)));
    }

    public void setMatrixMemcachedFactory(MatrixMemcachedFactory matrixMemcachedFactory) {
        this.matrixMemcachedFactory = matrixMemcachedFactory;
    }
}
