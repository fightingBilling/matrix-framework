package org.matrix.framework.core.platform.web.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.matrix.framework.core.collection.Key;
import org.matrix.framework.core.collection.KeyMap;
import org.matrix.framework.core.collection.TypeConversionException;
import org.matrix.framework.core.collection.TypeConverter;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.AssertUtils;
import org.matrix.framework.core.util.StringUtils;
import org.slf4j.Logger;

public class CookieContext {

    @SuppressWarnings("unused")
    private static final int DELETE_COOKIE_MAX_AGE = 0;
    private final Logger logger = LoggerFactory.getLogger(CookieContext.class);
    private HttpServletResponse httpServletResponse;
    private final KeyMap cookies = new KeyMap();
    private final TypeConverter typeConverter = new TypeConverter();

    public <T> T getCookie(Key<T> cookieKey) {
        try {
            return this.cookies.get(cookieKey);
        } catch (TypeConversionException e) {
            this.logger.warn("failed to convert cookie value", e);
        }
        return null;
    }

    /**
     * 调用一次该方法即意味着写回一个cookie.
     */
    public <T> void setCookie(Key<T> cookieKey, T value, CookieSpec cookieSpec) {
        Cookie cookie = new Cookie(cookieKey.name(), this.typeConverter.toString(value));
        if (cookieSpec.getHttpOnly() != null)
            cookie.setHttpOnly(cookieSpec.getHttpOnly().booleanValue());
        cookie.setPath(cookieSpec.getPath());
        if (cookieSpec.getSecure() != null)
            cookie.setSecure(cookieSpec.getSecure().booleanValue());
        if (cookieSpec.getMaxAge() != null)
            cookie.setMaxAge((int) cookieSpec.getMaxAge().toSeconds());
        if (cookieSpec.getDomain() != null) {
            cookie.setDomain(cookieSpec.getDomain());
        }
        AssertUtils.assertNotNull(this.httpServletResponse,
                "response is not injected, please check cookieInterceptor is added in WebConfig");
        this.httpServletResponse.addCookie(cookie);
    }

    /**
     * 使用原来的key新建一个cookie,以覆盖掉原来的cookie,达到删除的效果.
     */
    public <T> void deleteCookie(Key<T> cookieKey, CookieSpec cookieSpec) {
        Cookie cookie = new Cookie(cookieKey.name(), null);
        cookie.setMaxAge(0);
        cookie.setPath(cookieSpec.getPath());
        String domain = cookieSpec.getDomain();
        if (StringUtils.hasText(domain))
            cookie.setDomain(domain);

        AssertUtils.assertNotNull(this.httpServletResponse,
                "response is not injected, please check cookieInterceptor is added in WebConfig");
        this.httpServletResponse.addCookie(cookie);
    }

    void addCookie(String name, String value) {
        this.cookies.putString(name, value);
    }

    void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }
}
