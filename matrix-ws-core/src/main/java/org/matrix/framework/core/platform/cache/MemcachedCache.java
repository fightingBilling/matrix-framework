package org.matrix.framework.core.platform.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.MatrixException;
import org.matrix.framework.core.util.ExceptionUtils;
import org.matrix.framework.core.util.TimeLength;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * 使用memcached作为cache的实现类
 * 
 * @author pankai 2015年6月1日
 */
public class MemcachedCache implements Cache {

    // 操作memcached的超时时间设置为3秒.
    protected static final TimeLength MEMCACHED_TIME_OUT = TimeLength.seconds(3L);
    private final String cacheName;
    private final MemcachedClient memcachedClient;
    private final TimeLength expirationTime;
    private final String servers;

    public MemcachedCache(String cacheName, TimeLength expirationTime, MemcachedClient memcachedClient, String servers) {
        this.cacheName = cacheName;
        this.expirationTime = expirationTime;
        this.memcachedClient = memcachedClient;
        this.servers = servers;
    }

    @Override
    public String getName() {
        return this.cacheName;
    }

    /**
     * 返回该缓存的底层提供者.
     */
    @Override
    public Object getNativeCache() {
        return this.memcachedClient;
    }

    /**
     * 根据提供的key获取缓存的值
     */
    @Override
    public ValueWrapper get(Object key) {
        return get(key, 0);
    }

    /**
     * 试图从memcached中获取缓存的值,重试3次.
     */
    private ValueWrapper get(Object key, int counter) {
        Object value = null;
        try {
            value = this.memcachedClient.get(constructKey(key), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error(
                        "MEMCACHED SERVERS:[" + this.servers + "], cache get operition try 3 times failed;"
                                + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            get(key, counter + 1);
        }
        if (value == null)
            return null;
        return new SimpleValueWrapper(value);
    }

    /**
     * 试图从memcached中获取缓存的值,重试3次,并且将之转换为指定类型.
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        return get(key, type, 0);
    }

    /**
     * 将指定的key和value联结起来作为缓存.如果之前的缓存中已经有这个key了,则替换掉.
     */
    @Override
    public void put(Object key, Object value) {
        put(key, value, 0);
    }

    /**
     * 先将key与value放入memcached中,然后再将key本身也存储起来.
     */
    private void put(Object key, Object value, int counter) {
        if (null == value)
            return;
        try {
            this.memcachedClient.set(constructKey(key), (int) this.expirationTime.toSeconds(), value,
                    MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error(
                        "MEMCACHED SERVERS:[" + this.servers + "], cache put operition try 3 times failed;"
                                + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            put(key, value, counter + 1);
        }
        addKeyToCachedKeySet(key);
    }

    private void addKeyToCachedKeySet(Object key) {
        addKeyToCachedKeySet(key, 0);
    }

    private void addKeyToCachedKeySet(Object key, int counter) {
        Set<String> keys = fetchKeySet();
        keys.add(constructKey(key));
        try {
            this.memcachedClient.set(generateKeyOfKeySet(), 0, keys, MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error(
                        "MEMCACHED SERVERS:[" + this.servers
                                + "], cache addKeyToCachedKeySet operition try 3 times failed;"
                                + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            addKeyToCachedKeySet(key, counter + 1);
        }
    }

    private Set<String> fetchKeySet() {
        return fetchKeySet(0);
    }

    @SuppressWarnings("unchecked")
    private Set<String> fetchKeySet(int counter) {
        Object obj = null;
        try {
            obj = this.memcachedClient.get(generateKeyOfKeySet(), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error(
                        "MEMCACHED SERVERS:[" + this.servers + "], cache fetchKeySet operition try 3 times failed;"
                                + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            return fetchKeySet(counter + 1);
        }
        return obj == null ? new HashSet<String>() : (Set<String>) obj;
    }

    private String generateKeyOfKeySet() {
        return this.cacheName + "__KEY__";
    }

    /**
     * 4.1新加的方法;只有在指定key不存在的情况下放入.如果有key-value放入,返回null,否则返回已经存在的value
     */
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Object existingValue = get(key, 0);
        if (null == existingValue) {
            put(key, value, 0);
            return null;
        } else {
            return new SimpleValueWrapper(existingValue);
        }
    }

    /**
     * 使指定的key在缓存中失效
     */
    @Override
    public void evict(Object key) {
        evict(key, 0);
    }

    private void evict(Object key, int counter) {
        try {
            this.memcachedClient.delete(constructKey(key), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error(
                        "MEMCACHED SERVERS:[" + this.servers + "], cache evict operition try 3 times failed;"
                                + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            evict(Integer.valueOf(counter + 1));
        }
    }

    /**
     * 使该缓存中的所有key失效.
     */
    @Override
    public void clear() {
        clear(0);
    }

    private void clear(int counter) {
        Set<String> keys = fetchKeySet();
        try {
            for (String key : keys) {
                this.memcachedClient.delete(key);
            }
            this.memcachedClient.delete(generateKeyOfKeySet());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error(
                        "MEMCACHED SERVERS:[" + this.servers + "], cache clear operition try 3 times failed;"
                                + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            clear(counter + 1);
        }
    }

    private String constructKey(Object key) {
        return this.cacheName + ":" + key.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> T get(Object key, Class<T> type, int counter) {
        Object value = null;
        try {
            value = this.memcachedClient.get(constructKey(key), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error(
                        "MEMCACHED SERVERS:[" + this.servers + "], cache get operition try 3 times failed;"
                                + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            return get(key, type, counter + 1);
        }
        if (value == null)
            return null;
        if (value.getClass().isInstance(type))
            return (T) value;
        return null;
    }
}
