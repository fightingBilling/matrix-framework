package org.matrix.framework.core.platform.web.session;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.matrix.framework.core.collection.Key;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.SpringObjectFactory;
import org.matrix.framework.core.platform.web.cookie.CookieContext;
import org.matrix.framework.core.platform.web.cookie.CookieSpec;
import org.matrix.framework.core.platform.web.session.provider.LocalSessionProvider;
import org.matrix.framework.core.platform.web.session.provider.MemcachedSessionProvider;
import org.matrix.framework.core.platform.web.session.provider.RedisSessionProvider;
import org.matrix.framework.core.platform.web.session.provider.SessionProvider;
import org.matrix.framework.core.platform.web.site.SiteHelper;
import org.matrix.framework.core.settings.SiteSettings;
import org.matrix.framework.core.settings.SsoSettings;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 自定义session的实现. 根据SessionProvider的不同(暂)分为local,memcached,redis三种模式.
 * 
 * @author pankai 2015年6月18日
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {
    private final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    private final Key<String> cookieSessionId = Key.stringKey("SessionId");
    private final Key<String> cookieSecureSessionId = Key.stringKey("SecureSessionId");

    private static final String ATTRIBUTE_CONTEXT_INITIALIZED = SessionInterceptor.class.getName() + ".CONTEXT_INITIALIZED";
    @SuppressWarnings("unused")
    private static final String BEAN_NAME_SESSION_PROVIDER = "sessionProvider";
    private CookieContext cookieContext;
    private SessionContext sessionContext;
    private SecureSessionContext secureSessionContext;
    private SiteSettings siteSettings;
    private SessionProvider sessionProvider;
    private SpringObjectFactory springObjectFactory;
    private SsoSettings ssoSettings;

    /**
     * 初始化注册session提供者.目前定义了三种模式,local,memcached,redis. 模式的切换在SiteSettings里面进行更改.
     */
    @PostConstruct
    public void initialize() {
        SessionProviderType type = this.siteSettings.getSessionProviderType();
        if (SessionProviderType.MEMCACHED.equals(type))
            this.springObjectFactory.registerSingletonBean("sessionProvider", MemcachedSessionProvider.class);
        else if (SessionProviderType.LOCAL.equals(type))
            this.springObjectFactory.registerSingletonBean("sessionProvider", LocalSessionProvider.class);
        else if (SessionProviderType.REDIS.equals(type)) {
            this.springObjectFactory.registerSingletonBean("sessionProvider", RedisSessionProvider.class);
        } else {
            throw new IllegalStateException("unsupported session provider type, type=" + type);
        }
        this.sessionProvider = ((SessionProvider) this.springObjectFactory.getBean(SessionProvider.class));
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 非matrix framework所声明的controller不需要此interceptor进行任何处理.
         */
        if (!SiteHelper.isSiteController(handler))
            return true;

        Boolean initialized = (Boolean) request.getAttribute(ATTRIBUTE_CONTEXT_INITIALIZED);

        if (!Boolean.TRUE.equals(initialized)) {
            loadSession(this.sessionContext, this.cookieSessionId);

            if (request.isSecure()) {
                this.secureSessionContext.underSecureRequest();
                loadSession(this.secureSessionContext, this.cookieSecureSessionId);
            }

            request.setAttribute(ATTRIBUTE_CONTEXT_INITIALIZED, Boolean.TRUE);
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        saveAllSessions(request);
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        saveAllSessions(request);
    }

    /**
     * 会根据是否是安全连接,使用对应的cookie来保存信息.
     */
    private void saveAllSessions(HttpServletRequest request) {
        saveSession(this.sessionContext, this.cookieSessionId, false);
        if (request.isSecure())
            saveSession(this.secureSessionContext, this.cookieSecureSessionId, true);
    }

    private void loadSession(SessionContext sessionContext, Key<String> sessionIdCookieKey) {
        String sessionId = (String) this.cookieContext.getCookie(sessionIdCookieKey);

        if (sessionId != null) {
            /**
             * 已有session信息,先将session取出来,再重置session过期
             */
            String sessionData = this.sessionProvider.getAndRefreshSession(sessionId);
            if (sessionData != null) {
                sessionContext.setId(sessionId);
                sessionContext.loadSessionData(sessionData);
            } else {
                this.logger.debug("can not find session, generate new sessionId to replace old one");
                sessionContext.requireNewSessionId();
            }
        }
    }

    private void saveSession(SessionContext sessionContext, Key<String> sessionIdCookieKey, boolean secure) {
        if (sessionContext.changed()) {
            if (sessionContext.invalidated())
                deleteSession(sessionContext, sessionIdCookieKey);
            else {
                persistSession(sessionContext, sessionIdCookieKey, secure);
            }
            sessionContext.saved();
        }
    }

    /**
     * 删除session,先将session持久器里面的数据删除,然后将对应的cookie删除.
     */
    private void deleteSession(SessionContext sessionContext, Key<String> sessionIdCookieKey) {
        String sessionId = sessionContext.getId();
        if (sessionId == null) {
            return;
        }
        this.sessionProvider.clearSession(sessionId);
        CookieSpec spec = new CookieSpec();
        String domain = this.ssoSettings.getCookieDomain();
        if (StringUtils.hasText(domain))
            spec.setDomain(domain);
        spec.setPath(this.ssoSettings.getCookiePath());
        this.cookieContext.deleteCookie(sessionIdCookieKey, spec);
    }

    /**
     * session发生了变化调用此方法对session进行"持久化"
     */
    private void persistSession(SessionContext sessionContext, Key<String> sessionIdCookieKey, boolean secure) {
        //先获取sessionContext中的id.
        String sessionId = sessionContext.getId();
        //如果还未生成sessionId要重新生成sessionId.
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            sessionContext.setId(sessionId);
            CookieSpec spec = new CookieSpec();
            /**
             * 框架内部使用的cookie不会暴露给客户端.
             */
            spec.setHttpOnly(Boolean.valueOf(true));
            spec.setPath(this.ssoSettings.getCookiePath());
            /**
             * 模拟原本session的实现,将cookie的max-age属性设置为负数,这意味着浏览器关闭之后,cookie会被立即删除. cookie上带着的相关session的key也不复存在.session失效.
             */
            spec.setMaxAge(CookieSpec.MAX_AGE_SESSION_SCOPE);
            spec.setSecure(Boolean.valueOf(secure));
            String domain = this.ssoSettings.getCookieDomain();
            if (StringUtils.hasText(domain))
                spec.setDomain(domain);
            /**
             * 往CookieContext中设置的cookie都会通过org.matrix.framework.core.platform. web.cookie.CookieInterceptor,重置回到CookieContext中
             */
            //将带有sessionId的cookie写回.
            this.cookieContext.setCookie(sessionIdCookieKey, sessionId, spec);
        }
        //存入sessionProvider的数据是sessionId加数据.
        this.sessionProvider.saveSession(sessionId, sessionContext.getSessionData());
    }

    @Inject
    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Inject
    public void setSecureSessionContext(SecureSessionContext secureSessionContext) {
        this.secureSessionContext = secureSessionContext;
    }

    @Inject
    public void setSiteSettings(SiteSettings siteSettings) {
        this.siteSettings = siteSettings;
    }

    @Inject
    public void setCookieContext(CookieContext cookieContext) {
        this.cookieContext = cookieContext;
    }

    @Inject
    public void setSpringObjectFactory(SpringObjectFactory springObjectFactory) {
        this.springObjectFactory = springObjectFactory;
    }

    @Inject
    public void setSsoSettings(SsoSettings ssoSettings) {
        this.ssoSettings = ssoSettings;
    }
}