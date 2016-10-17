package org.matrix.framework.core.platform.cache.support.lru;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.matrix.framework.core.platform.cache.Cache;

/**
 * 本地LRU缓存的实现
 * 
 * @author pankai 2015年8月21日
 */
public class LRUCache implements Cache {

    private final Map<Object, Object> store;

    public LRUCache(int size) {
        // 第三个参数设置为true,为访问顺序
        this.store = new LinkedHashMap<Object, Object>(0, 0.75f, true) {

            private static final long serialVersionUID = 8259098511531127453L;

            @Override
            protected boolean removeEldestEntry(Entry<Object, Object> eldest) {
                return size() > size;
            }

        };
    }

    @Override
    public void put(Object key, Object value) {
        synchronized (store) {
            store.put(key, value);
        }
    }

    @Override
    public Object get(Object key) {
        synchronized (store) {
            return store.get(key);
        }
    }

}
