package org.matrix.framework.core.platform.cache.guava.support;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.cache.CacheStats;

/**
 * TODO:增加访问限制.
 * @author pankai
 * Nov 2, 2015
 */
@Controller
public class GuavaCacheStatsController extends MatrixRestController {

    private CacheManager cacheManager;

    @RequestMapping(value = "/monitor/cachestats", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public List<GuavaCacheStats> cacheStats() {
        List<GuavaCacheStats> list = new ArrayList<GuavaCacheStats>();
        for (String cacheName : cacheManager.getCacheNames()) {
            GuavaCache cache = (GuavaCache) cacheManager.getCache(cacheName);
            CacheStats cacheStats = cache.getNativeCache().stats();
            GuavaCacheStats guavaCacheStats = new GuavaCacheStats();
            guavaCacheStats.setCacheName(cacheName);
            guavaCacheStats.setHitCount(cacheStats.hitCount());
            guavaCacheStats.setMissCount(cacheStats.missCount());
            guavaCacheStats.setLoadSuccessCount(cacheStats.loadSuccessCount());
            guavaCacheStats.setLoadExceptionCount(cacheStats.loadExceptionCount());
            guavaCacheStats.setTotalLoadTime(cacheStats.totalLoadTime());
            guavaCacheStats.setEvictionCount(cacheStats.evictionCount());
            guavaCacheStats.setHitRate(cacheStats.hitRate());
            guavaCacheStats.setLoadExceptionRate(cacheStats.loadExceptionRate());
            guavaCacheStats.setMissRate(cacheStats.missRate());
            list.add(guavaCacheStats);
        }
        return list;
    }

    @Inject
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}
