package org.matrix.framework.core.platform.cache.guava.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuavaCacheStats {

    @JsonProperty("cache_name")
    private String cacheName;
    @JsonProperty("hit_count")
    private long hitCount;
    @JsonProperty("hit_rate")
    private double hitRate;
    @JsonProperty("miss_count")
    private long missCount;
    @JsonProperty("miss_rate")
    private double missRate;
    @JsonProperty("load_success_count")
    private long loadSuccessCount;
    @JsonProperty("load_exception_count")
    private long loadExceptionCount;
    @JsonProperty("load_exception_rate")
    private double loadExceptionRate;
    @JsonProperty("total_load_time")
    private long totalLoadTime;
    @JsonProperty("eviction_count")
    private long evictionCount;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public double getHitRate() {
        return hitRate;
    }

    public void setHitRate(double hitRate) {
        this.hitRate = hitRate;
    }

    public long getMissCount() {
        return missCount;
    }

    public void setMissCount(long missCount) {
        this.missCount = missCount;
    }

    public double getMissRate() {
        return missRate;
    }

    public void setMissRate(double missRate) {
        this.missRate = missRate;
    }

    public long getLoadSuccessCount() {
        return loadSuccessCount;
    }

    public void setLoadSuccessCount(long loadSuccessCount) {
        this.loadSuccessCount = loadSuccessCount;
    }

    public long getLoadExceptionCount() {
        return loadExceptionCount;
    }

    public void setLoadExceptionCount(long loadExceptionCount) {
        this.loadExceptionCount = loadExceptionCount;
    }

    public double getLoadExceptionRate() {
        return loadExceptionRate;
    }

    public void setLoadExceptionRate(double loadExceptionRate) {
        this.loadExceptionRate = loadExceptionRate;
    }

    public long getTotalLoadTime() {
        return totalLoadTime;
    }

    public void setTotalLoadTime(long totalLoadTime) {
        this.totalLoadTime = totalLoadTime;
    }

    public long getEvictionCount() {
        return evictionCount;
    }

    public void setEvictionCount(long evictionCount) {
        this.evictionCount = evictionCount;
    }

}
