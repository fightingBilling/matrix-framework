package org.matrix.framework.core.settings;

import org.matrix.framework.core.platform.web.RuntimeEnvironment;
import org.matrix.framework.core.platform.web.session.SessionProviderType;
import org.matrix.framework.core.util.TimeLength;

/**
 * 站点设置
 * 
 * @author pankai 2015年6月17日
 */
public class SiteSettings {
    private String errorPage;
    private String resourceNotFoundPage;
    private String sessionTimeOutPage;
    private String staticDir;
    private String nfsDir;
    private String jsDir;
    private String cssDir;
    private SessionProviderType sessionProviderType = SessionProviderType.LOCAL;
    /**
     * 默认的session到期时间,15分钟.
     */
    private TimeLength sessionTimeOut = TimeLength.minutes(15L);
    private String loginUrl;
    private String originalUrl;
    private RuntimeEnvironment environment = RuntimeEnvironment.DEV;
    private String version;

    public String getErrorPage() {
        return this.errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    public String getResourceNotFoundPage() {
        return this.resourceNotFoundPage;
    }

    public void setResourceNotFoundPage(String resourceNotFoundPage) {
        this.resourceNotFoundPage = resourceNotFoundPage;
    }

    public String getSessionTimeOutPage() {
        return this.sessionTimeOutPage;
    }

    public void setSessionTimeOutPage(String sessionTimeOutPage) {
        this.sessionTimeOutPage = sessionTimeOutPage;
    }

    public TimeLength getSessionTimeOut() {
        return this.sessionTimeOut;
    }

    public void setSessionTimeOut(TimeLength sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public SessionProviderType getSessionProviderType() {
        return this.sessionProviderType;
    }

    public void setSessionProviderType(SessionProviderType sessionProviderType) {
        this.sessionProviderType = sessionProviderType;
    }

    public String getJsDir() {
        return this.jsDir;
    }

    public void setJsDir(String jsDir) {
        this.jsDir = jsDir;
    }

    public String getCssDir() {
        return this.cssDir;
    }

    public void setCssDir(String cssDir) {
        this.cssDir = cssDir;
    }

    public String getNfsDir() {
        return this.nfsDir;
    }

    public void setNfsDir(String nfsDir) {
        this.nfsDir = nfsDir;
    }

    public String getLoginUrl() {
        return this.loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getOriginalUrl() {
        return this.originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getStaticDir() {
        return this.staticDir;
    }

    public void setStaticDir(String staticDir) {
        this.staticDir = staticDir;
    }

    public RuntimeEnvironment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(RuntimeEnvironment environment) {
        this.environment = environment;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
