package org.matrix.framework.core.platform;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.matrix.framework.core.platform.web.request.RequestContext;
import org.matrix.framework.core.platform.web.request.RequestContextInterceptor;
import org.matrix.framework.core.platform.web.velocity.VelocityAccess;
import org.matrix.framework.core.util.I18nUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.context.WebApplicationContext;

public class DefaultSiteWebConfig extends DefaultWebConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VelocityEngineFactoryBean velocityEngineFactory() {
        VelocityEngineFactoryBean velocityEngineFactory = new VelocityEngineFactoryBean();
        velocityEngineFactory.setResourceLoaderPath("/WEB-INF/velocity/");
        velocityEngineFactory.setPreferFileSystemAccess(false);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("default.contentType", "text/html; charset=utf-8");
        map.put("output.encoding", "utf-8");
        map.put("input.encoding", "utf-8");
        velocityEngineFactory.setVelocityPropertiesMap(map);
        return velocityEngineFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VelocityEngine velocityEngine() throws VelocityException, IOException {
        return velocityEngineFactory().getObject();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VelocityAccess vlocityAccess() {
        return new VelocityAccess();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RequestContext requestContext() {
        return new RequestContext();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public I18nUtil i18nUtil() {
        return new I18nUtil();
    }

    @Bean
    public RequestContextInterceptor requestContextInterceptor() {
        return new RequestContextInterceptor();
    }
}