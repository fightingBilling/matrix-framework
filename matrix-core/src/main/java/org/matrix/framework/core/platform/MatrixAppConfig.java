package org.matrix.framework.core.platform;

import org.matrix.framework.core.platform.im4j.service.MatrixIm4jService;
import org.matrix.framework.core.platform.im4j.service.manager.ThumbnailManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * AppConfig该继承这个类.然而在v2版本之前的应用,这个类可能被跳过.
 * @author pankai
 * 2016年4月7日
 */
public abstract class MatrixAppConfig extends DefaultAppConfig {

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true);
        return resolver;
    }

    @Bean
    public ThumbnailManager thumbnailManager() {
        return new ThumbnailManager();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MatrixIm4jService matrixIm4jService() {
        return new MatrixIm4jService();
    }

}
