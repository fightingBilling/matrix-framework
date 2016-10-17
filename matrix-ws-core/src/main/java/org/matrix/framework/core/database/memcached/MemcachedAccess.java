package org.matrix.framework.core.database.memcached;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.collections.MapUtils;
import org.matrix.framework.core.collection.converter.JSONConverter;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.MatrixException;
import org.matrix.framework.core.util.ExceptionUtils;
import org.matrix.framework.core.util.TimeLength;
import org.slf4j.Logger;

@SuppressWarnings("all")
public class MemcachedAccess {

    protected static final TimeLength MEMCACHED_TIME_OUT = TimeLength.seconds(3L);

    private final Logger logger = LoggerFactory.getLogger(MemcachedAccess.class);

    private static final JSONConverter JSONCONVERTER = new JSONConverter();
    private final MemcachedClient memcachedClient;

    public MemcachedAccess(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public Map<String, Object> batchGet(Collection<String> keys) {
        Map allData = null;
        try {
            allData = this.memcachedClient.get(keys, MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
            throw new MatrixException("Operition memcached exception,detail in log file.");
        }
        if (MapUtils.isEmpty(allData)) {
            this.logger.warn("can not find bulk data");
            return null;
        }
        return allData;
    }

    public <T> T get(String key, Class<T> clazz, Class<?>... elementClasses) {
        Object value = null;
        try {
            value = this.memcachedClient.get(key, MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
            throw new MatrixException("Operition memcached exception,detail in log file.");
        }
        if (null == value) {
            this.logger.warn("can not find data, key=" + key);
            return null;
        }
        return JSONCONVERTER.fromString(clazz, String.valueOf(value), elementClasses);
    }

    public <T> T gets(String key, Class<T> clazz, Class<?>[] elementClasses) {
        GetsResponse response = null;
        try {
            response = this.memcachedClient.gets(key, MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
            throw new MatrixException("Operition memcached exception,detail in log file.");
        }

        if (null == response) {
            this.logger.warn("can not find data, key=" + key);
            return null;
        }

        Object casValue = response.getValue();

        if (null == casValue) {
            this.logger.warn("can not find data, key=" + key);
            return null;
        }
        return JSONCONVERTER.fromString(clazz, (String) casValue, elementClasses);
    }

    public <T> T getAndRefresh(String key, Class<T> clazz, TimeLength timeLength, Class<?>[] elementClasses) {
        Object casValue = null;
        try {
            casValue = this.memcachedClient.getAndTouch(key, (int) timeLength.toSeconds(), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
            throw new MatrixException("Operition memcached exception,detail in log file.");
        }
        if (casValue == null) {
            this.logger.warn("can not find data, key=" + key);
            return null;
        }
        return JSONCONVERTER.fromString(clazz, (String) casValue, elementClasses);
    }

    public <T> void set(String key, TimeLength timeLength, T data, Class<?>... elementClasses) {
        try {
            this.memcachedClient.set(key, (int) timeLength.toSeconds(), JSONCONVERTER.toString(data, elementClasses), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
            throw new MatrixException("Operition memcached exception,detail in log file.");
        }
    }

    public <T> boolean add(String key, TimeLength timeLength, T data, Class<?>[] elementClasses) {
        try {
            return this.memcachedClient.add(key, (int) timeLength.toSeconds(), JSONCONVERTER.toString(data, elementClasses), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
        }
        throw new MatrixException("Operition memcached exception,detail in log file.");
    }

    public boolean delete(String key) {
        try {
            return this.memcachedClient.delete(key, MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
        }
        throw new MatrixException("Operition memcached exception,detail in log file.");
    }

    public boolean flush() {
        try {
            this.memcachedClient.flushAll(MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
            throw new MatrixException("Operition memcached exception,detail in log file.");
        }
        return true;
    }

    public MemcachedClient getMemcachedClient() {
        return this.memcachedClient;
    }

}
