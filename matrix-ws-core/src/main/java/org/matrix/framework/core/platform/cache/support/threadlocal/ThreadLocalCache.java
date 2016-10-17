package org.matrix.framework.core.platform.cache.support.threadlocal;

import java.util.HashMap;
import java.util.Map;

import org.matrix.framework.core.platform.cache.Cache;

/**
 * ThreadLocal cache
 * 
 * @author pankai 2015年8月21日
 */
public class ThreadLocalCache implements Cache {

    private final ThreadLocal<Map<Object, Object>> store;

    public ThreadLocalCache() {
        this.store = new ThreadLocal<Map<Object, Object>>() {

            @Override
            protected Map<Object, Object> initialValue() {
                return new HashMap<Object, Object>();
            }
        };
    }

    @Override
    public void put(Object key, Object value) {
        this.store.get().put(key, value);
    }

    @Override
    public Object get(Object key) {
        return this.store.get().get(key);
    }

}
