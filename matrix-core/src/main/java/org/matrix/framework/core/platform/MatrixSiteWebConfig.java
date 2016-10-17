package org.matrix.framework.core.platform;

import java.util.Properties;

import org.matrix.framework.core.platform.web.cookie.CookieContext;
import org.matrix.framework.core.platform.web.cookie.CookieInterceptor;
import org.matrix.framework.core.platform.web.freemarker.view.DefaultFreeMarkerConfigurer;
import org.matrix.framework.core.platform.web.freemarker.view.DefaultFreemarkerView;
import org.matrix.framework.core.platform.web.freemarker.view.DefaultFreemarkerViewResolver;
import org.matrix.framework.core.platform.web.session.SecureSessionContext;
import org.matrix.framework.core.platform.web.session.SessionContext;
import org.matrix.framework.core.platform.web.session.SessionInterceptor;
import org.matrix.framework.core.platform.web.site.scheme.HTTPSchemeEnforceInterceptor;
import org.matrix.framework.core.settings.CDNSettings;
import org.matrix.framework.core.settings.DefaultCDNSettings;
import org.matrix.framework.core.settings.DeploymentSettings;
import org.matrix.framework.core.settings.SiteSettings;
import org.matrix.framework.core.settings.SsoSettings;
import org.matrix.framework.core.settings.ThumbnailSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

public class MatrixSiteWebConfig extends DefaultSiteWebConfig {

    /**
     * Freemarker的配置器,如果应用了freemarker解析器则必须要有这个
     */
    @Bean
    public DefaultFreeMarkerConfigurer freeMarkerConfig() {
        DefaultFreeMarkerConfigurer configurer = new DefaultFreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("/");
        Properties settings = new Properties();
        setFreemarkerSettings(settings);
        configurer.setFreemarkerSettings(settings);
        return configurer;
    }

    protected void setFreemarkerSettings(Properties settings) {
        settings.setProperty("default_encoding", "UTF-8");
        settings.setProperty("url_escaping_charset", "UTF-8");
        settings.setProperty("number_format", "0.##");
    }

    /**
     * Freemarker的解析器
     */
    @Bean
    public DefaultFreemarkerViewResolver freemarkerViewResolver() {
        DefaultFreemarkerViewResolver resolver = new DefaultFreemarkerViewResolver();
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setViewClass(DefaultFreemarkerView.class);
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setExposeRequestAttributes(true);
        return resolver;
    }

    /**
     * 站点设置.
     */
    @Bean
    public SiteSettings siteSettings() {
        return new SiteSettings();
    }

    /**
     * 发布设置
     */
    @Bean
    public DeploymentSettings deploymentSettings() {
        return new DeploymentSettings();
    }

    /**
     * 单点登录设置
     */
    @Bean
    public SsoSettings ssoSettings() {
        return new SsoSettings();
    }

    @Bean
    public CDNSettings cdnSettings() {
        return new DefaultCDNSettings();
    }

    /**
     * 图片处理设置
     */
    @Bean
    public ThumbnailSettings thumbnailSettings() {
        return new ThumbnailSettings();
    }

    @Bean
    public HTTPSchemeEnforceInterceptor httpSchemeEnforceInterceptor() {
        return new HTTPSchemeEnforceInterceptor();
    }

    @Bean
    public CookieInterceptor cookieInterceptor() {
        return new CookieInterceptor();
    }

    @Bean
    public SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public CookieContext cookieContext() {
        return new CookieContext();
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SessionContext sessionContext() {
        return new SessionContext();
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SecureSessionContext secureSessionContext() {
        return new SecureSessionContext();
    }
}
