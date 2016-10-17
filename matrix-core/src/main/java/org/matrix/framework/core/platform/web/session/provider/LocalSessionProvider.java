package org.matrix.framework.core.platform.web.session.provider;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.matrix.framework.core.platform.monitor.ServiceStatus;
import org.matrix.framework.core.settings.SiteSettings;
import org.matrix.framework.core.util.DateUtils;

/**
 * 本地session提供.使用ConcurrentHashMap来保存session信息.
 * 
 * @author pankai 2015年6月18日
 */
public class LocalSessionProvider implements SessionProvider {

    private final Map<String, SessionValue> values = new ConcurrentHashMap<String, SessionValue>();
    private SiteSettings siteSettings;

    @Override
    public String getServiveName() {
        return "LocalSession";
    }

    @Override
    public String getAndRefreshSession(String sessionId) {
        // 根据sessionId未找到数据,返回空.
        SessionValue sessionValue = (SessionValue) this.values.get(sessionId);
        if (sessionValue == null)
            return null;
        // session已经过期,返回空.
        if (new Date().after(sessionValue.getExpiredDate())) {
            this.values.remove(sessionId);
            return null;
        }

        String data = sessionValue.getData();
        this.values.put(sessionId, new SessionValue(expirationTime(), data));
        return data;
    }

    @Override
    public void saveSession(String sessionId, String sessionData) {
        this.values.put(sessionId, new SessionValue(expirationTime(), sessionData));
    }

    @Override
    public void clearSession(String sessionId) {
        this.values.remove(sessionId);
    }

    private Date expirationTime() {
        return DateUtils.add(new Date(), 13, (int) this.siteSettings.getSessionTimeOut().toSeconds());
    }

    @Override
    public ServiceStatus getServiceStatus() throws Exception {
        return ServiceStatus.UP;
    }

    @Inject
    public void setSiteSettings(SiteSettings siteSettings) {
        this.siteSettings = siteSettings;
    }

    private static class SessionValue {
        private final Date expiredDate;
        private final String data;

        public SessionValue(Date expiredDate, String data) {
            this.expiredDate = expiredDate;
            this.data = data;
        }

        public Date getExpiredDate() {
            return this.expiredDate;
        }

        public String getData() {
            return this.data;
        }
    }
}