package org.matrix.framework.core.database.memcached;

import java.util.List;

import javax.inject.Inject;

public class MatrixMemcachedFactory extends MemcachedFactory {

    protected MemcachedGroups memcachedGroups;

    public List<CacheGroup> getCacheGroups() {
        return this.memcachedGroups.getCacheGroups();
    }

    @Inject
    public void setMemcachedGroups(MemcachedGroups memcachedGroups) {
        this.memcachedGroups = memcachedGroups;
    }

    public boolean binarySupport() {
        return true;
    }
}
