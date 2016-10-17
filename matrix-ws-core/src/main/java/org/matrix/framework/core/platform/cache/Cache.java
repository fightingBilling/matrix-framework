package org.matrix.framework.core.platform.cache;

/**
 * 缓存抽象.
 * 
 * @author pankai 2015年8月21日
 */
public interface Cache {

    void put(Object key, Object value);

    Object get(Object key);

}
