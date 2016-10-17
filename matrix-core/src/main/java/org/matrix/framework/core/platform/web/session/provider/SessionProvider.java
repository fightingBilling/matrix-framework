package org.matrix.framework.core.platform.web.session.provider;

import org.matrix.framework.core.platform.monitor.ServiceMonitor;

/**
 * 自定义session存储方式的接口
 * 
 * @author pankai 2015年6月18日
 */
public abstract interface SessionProvider extends ServiceMonitor {

    /**
     * 取得序列化后的session数据,并且重置session时间.
     */
    public abstract String getAndRefreshSession(String sessionId);

    /**
     * 保存键值对到session中.
     */
    public abstract void saveSession(String key, String value);

    /**
     * 清除session
     */
    public abstract void clearSession(String sessionId);
}