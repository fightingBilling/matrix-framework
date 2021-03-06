package org.matrix.framework.core.settings;

import java.util.HashMap;
import java.util.Map;

import org.matrix.framework.core.collection.Key;

public class SsoSettings {
    private String ssoConstantsClassName;
    private String globalDns;
    private String cookieDomain;
    private String cookiePath;
    private final Map<Key<SsoSite>, SsoSite> ssoSites;

    public SsoSettings() {
        this.ssoSites = new HashMap<Key<SsoSite>, SsoSite>();
    }

    public Map<Key<SsoSite>, SsoSite> getSsoSites() {
        return this.ssoSites;
    }

    public String getGlobalDns() {
        return this.globalDns;
    }

    public void setGlobalDns(String globalDns) {
        this.globalDns = globalDns;
    }

    public String getCookieDomain() {
        return this.cookieDomain;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public String getCookiePath() {
        return this.cookiePath;
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public String getSsoConstantsClassName() {
        return this.ssoConstantsClassName;
    }

    public void setSsoConstantsClassName(String ssoConstantsClassName) {
        this.ssoConstantsClassName = ssoConstantsClassName;
    }

    public static class SsoSite {
        private final String dns;
        private final String deployContext;
        private String accessPoint;

        public SsoSite(String dns, String deployContext, String accessPoint) {
            this.dns = dns;
            this.deployContext = deployContext;
            this.accessPoint = accessPoint;
        }

        public String getDns() {
            return this.dns;
        }

        public String getDeployContext() {
            return this.deployContext;
        }

        public String getAccessPoint() {
            return this.accessPoint;
        }

        public void setAccessPoint(String accessPoint) {
            this.accessPoint = accessPoint;
        }
    }
}