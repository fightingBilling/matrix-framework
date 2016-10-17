package org.matrix.framework.core.platform.throttling.provider;

/**
 * 请求计数器接口
 * 
 * @author pankai 2015年9月20日
 */
public interface RequestLimitProvider {

    /**
     * 根据key加1,然后返回加1之后的值.
     */
    public Long incrementAndGet(String key, Long expire);

    /**
     * 设置一个带有过期期限键
     */
    public void set(String key, Long expire);

}
