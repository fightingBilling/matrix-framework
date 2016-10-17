package org.matrix.framework.core.platform.web.session.provider;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.matrix.framework.core.database.memcached.MatrixMemcachedFactory;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.MatrixException;
import org.matrix.framework.core.platform.monitor.ServiceStatus;
import org.matrix.framework.core.settings.SiteSettings;
import org.matrix.framework.core.util.ExceptionUtils;
import org.matrix.framework.core.util.TimeLength;

/**
 * 使用memcached作为session的存储.
 * 
 * @Note 末尾的setMatrixMemcachedFactory方法,已经定义了memcached的分组信息"SESSIONGROUP"
 * @author pankai 2015年6月18日
 */
public class MemcachedSessionProvider implements SessionProvider {

    protected static final TimeLength MEMCACHED_TIME_OUT = TimeLength.seconds(3L);
    private MemcachedClient memcachedClient;
    private String servers;
    private SiteSettings siteSettings;

    @Override
    public String getAndRefreshSession(String sessionId) {
        return getAndRefreshSession(sessionId, 0);
    }

    /**
     * 获取session,至多3次失败.
     * 
     * @param sessionId
     * @param counter
     * @return
     */
    private String getAndRefreshSession(String sessionId, int counter) {
        String sessionKey = getCacheKey(sessionId);
        Object cacheValue = null;
        try {
            cacheValue = getClient().getAndTouch(sessionKey, expirationTime(), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error("MEMCACHED SERVERS:[" + this.servers + "], getAndRefreshSession operition try 3 times failed;" + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            return getAndRefreshSession(sessionId, counter + 1);
        }

        if (cacheValue == null) {
            LoggerFactory.MEMCACHEDLOGGER.getLogger().warn("can not find session or session expired, sessionKey=" + sessionKey);
            return null;
        }
        return (String) cacheValue;
    }

    @Override
    public void saveSession(String sessionId, String sessionData) {
        saveSession(sessionId, sessionData, 0);
    }

    /**
     * 将session数据存入memcached中.重试3次
     */
    private void saveSession(String sessionId, String sessionData, int counter) {
        try {
            getClient().set(getCacheKey(sessionId), expirationTime(), sessionData, MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error("MEMCACHED SERVERS:[" + this.servers + "], saveSession operition try 3 times failed;" + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            saveSession(sessionId, sessionData, counter + 1);
        }
    }

    @Override
    public void clearSession(String sessionId) {
        clearSession(sessionId, 0);
    }

    private void clearSession(String sessionId, int counter) {
        try {
            getClient().delete(getCacheKey(sessionId), MEMCACHED_TIME_OUT.toMilliseconds());
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error("MEMCACHED SERVERS:[" + this.servers + "], clearSession operition try 3 times failed;" + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition memcached timeout,detail in log file.");
            }
            clearSession(sessionId, counter + 1);
        }
    }

    @Override
    public String getServiveName() {
        return "MemcachedSession";
    }

    @Override
    public ServiceStatus getServiceStatus() throws Exception {
        Collection<InetSocketAddress> availableServers = getClient().getAvailableServers();
        return availableServers.isEmpty() ? ServiceStatus.DOWN : ServiceStatus.UP;
    }

    private String getCacheKey(String sessionId) {
        return "session:" + sessionId;
    }

    private int expirationTime() {
        return (int) this.siteSettings.getSessionTimeOut().toSeconds();
    }

    public MemcachedClient getClient() {
        return this.memcachedClient;
    }

    @Inject
    public void setSiteSettings(SiteSettings siteSettings) {
        this.siteSettings = siteSettings;
    }

    @Inject
    public void setMatrixMemcachedFactory(MatrixMemcachedFactory matrixMemcachedFactory) {
        this.memcachedClient = matrixMemcachedFactory.getClient("SESSIONGROUP");
        this.servers = matrixMemcachedFactory.getServers("SESSIONGROUP");
    }
}