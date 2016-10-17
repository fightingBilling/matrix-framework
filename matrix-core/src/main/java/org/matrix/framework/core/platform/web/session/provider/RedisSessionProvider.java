package org.matrix.framework.core.platform.web.session.provider;

import javax.inject.Inject;

import org.matrix.framework.core.database.redis.RedisAccess;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.MatrixException;
import org.matrix.framework.core.platform.monitor.ServiceStatus;
import org.matrix.framework.core.settings.SiteSettings;
import org.matrix.framework.core.util.ExceptionUtils;

/**
 * 使用Redis作为session提供
 * 
 * @TODO 该方案未像memcached分组.使用全局唯一的redis源.
 * @author pankai 2015年8月7日
 */
public class RedisSessionProvider implements SessionProvider {

    private SiteSettings siteSettings;
    private RedisAccess redisAccess;

    @Override
    public ServiceStatus getServiceStatus() throws Exception {
        return redisAccess.isConnected() ? ServiceStatus.UP : ServiceStatus.DOWN;
    }

    @Override
    public String getServiveName() {
        return "RedisSession";
    }

    @Override
    public String getAndRefreshSession(String sessionId) {
        return getAndRefreshSession(sessionId, 0);
    }

    private String getAndRefreshSession(String sessionId, int counter) {
        String sessionKey = getCacheKey(sessionId);
        String cacheValue = null;
        try {
            cacheValue = redisAccess.get(sessionKey, expirationTime());
        } catch (Exception e) {
            if (counter > 3) {
                LoggerFactory.REDISLOGGER.getLogger().error("Redis getAndRefreshSession operition try 3 times failed;" + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition redis failed,detail in log file.");
            }
            return getAndRefreshSession(sessionId, counter + 1);
        }
        if (cacheValue == null) {
            LoggerFactory.REDISLOGGER.getLogger().warn("can not find session or session expired, sessionKey=" + sessionKey);
            return null;
        }
        return cacheValue;
    }

    @Override
    public void saveSession(String key, String value) {
        saveSession(key, value, 0);
    }

    /**
     * 将session数据存入redis中.重试3次
     */
    private void saveSession(String sessionId, String sessionData, int counter) {
        try {
            redisAccess.set(getCacheKey(sessionId), sessionData, expirationTime());
        } catch (Exception e) {
            if (counter > 3) {
                LoggerFactory.REDISLOGGER.getLogger().error("Redis getAndRefreshSession operition try 3 times failed;" + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition redis failed,detail in log file.");
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
            redisAccess.del(getCacheKey(sessionId));
        } catch (Exception e) {
            if (counter > 3) {
                LoggerFactory.MEMCACHEDLOGGER.getLogger().error("Redis getAndRefreshSession operition try 3 times failed;" + ExceptionUtils.stackTrace(e));
                throw new MatrixException("Operition redis failed,detail in log file.");
            }
            clearSession(sessionId, counter + 1);
        }
    }

    private int expirationTime() {
        return (int) this.siteSettings.getSessionTimeOut().toSeconds();
    }

    private String getCacheKey(String sessionId) {
        return "session:" + sessionId;
    }

    @Inject
    public void setSiteSettings(SiteSettings siteSettings) {
        this.siteSettings = siteSettings;
    }

    @Inject
    public void setRedisAccess(RedisAccess redisAccess) {
        this.redisAccess = redisAccess;
    }

}
