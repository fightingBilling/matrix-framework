package org.matrix.framework.core.platform;

import org.junit.Test;
import org.matrix.framework.core.platform.cache.support.lru.LRUCache;
import org.matrix.framework.core.util.StopWatch;

public class LRUCacheTest {

    @Test
    public void test0() {
        int maxSize = 5;
        StopWatch stopWatch = new StopWatch();
        LRUCache lruCache = new LRUCache(maxSize);
        for (int i = 0; i < 6; i++) {
            if (i == 5) {
                lruCache.get(0);
            }
            lruCache.put(i, i);
        }
        System.out.println("elapsedTime:" + stopWatch.elapsedTime());
        System.out.println(lruCache.get(0));
    }

}
