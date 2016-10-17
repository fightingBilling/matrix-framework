package org.matrix.framework.core.platform.web.cookie;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.matrix.framework.core.collection.Key;
import org.matrix.framework.core.platform.web.site.SiteHelper;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 初始化CookieContext,将所有已有的cookie加到CookieContext实例中且一定会有一个名为clientId的cookie被加入(如原本不存在).
 * 
 * @author pankai 2015年6月18日
 */
public class CookieInterceptor extends HandlerInterceptorAdapter {

    private static final String ATTRIBUTE_CONTEXT_INITIALIZED = CookieInterceptor.class.getName() + ".ATTRIBUTE_CONTEXT_INITIALIZED";

    @SuppressWarnings("unused")
    private static final String CLIENT_ID = "clientId";

    /**
     * CookieContext的scope被设置为request.意味着每个request都会产生一个新的CookieContext对象.
     */
    private CookieContext cookieContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!SiteHelper.isSiteController(handler)) {
            return true;
        }
        Boolean initialized = (Boolean) request.getAttribute(ATTRIBUTE_CONTEXT_INITIALIZED);
        if (!Boolean.TRUE.equals(initialized)) {
            /**
             * 把所有cookie加到CookieContext实例中.在matrix中,包含clientId及SessionId.
             */
            Cookie[] cookies = request.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    this.cookieContext.addCookie(cookie.getName(), cookie.getValue());
                }
            }
            this.cookieContext.setHttpServletResponse(response);
            /**
             * 如果CookieContext实例中没有clientId,就随机用UUID生成.
             */
            if (this.cookieContext.getCookie(Key.stringKey("clientId")) == null) {
                this.cookieContext.setCookie(Key.stringKey("clientId"), UUID.randomUUID().toString(), new CookieSpec(CookieSpec.MAX_AGE_SESSION_SCOPE));
            }
            request.setAttribute(ATTRIBUTE_CONTEXT_INITIALIZED, Boolean.TRUE);
        }
        return true;
    }

    @Inject
    public void setCookieContext(CookieContext cookieContext) {
        this.cookieContext = cookieContext;
    }

}
