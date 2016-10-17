package org.matrix.framework.core.platform.web.cookie;

import org.matrix.framework.core.util.TimeLength;

/**
 * cookie的特殊定义.
 * 
 * @author pankai 2015年6月18日
 */
public class CookieSpec {
    public static final TimeLength MAX_AGE_SESSION_SCOPE = TimeLength.seconds(-1L);
    public static final TimeLength ONE_YEAR = TimeLength.days(365L);
    private String path;
    private Boolean httpOnly;
    private Boolean secure;
    private TimeLength maxAge;
    private String domain;

    public CookieSpec() {
    }

    public CookieSpec(TimeLength maxAge) {
        this.maxAge = maxAge;
        this.path = "/";
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getHttpOnly() {
        return this.httpOnly;
    }

    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public Boolean getSecure() {
        return this.secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public TimeLength getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(TimeLength maxAge) {
        this.maxAge = maxAge;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
