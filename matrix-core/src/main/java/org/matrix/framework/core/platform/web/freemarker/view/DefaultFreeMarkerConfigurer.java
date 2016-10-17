package org.matrix.framework.core.platform.web.freemarker.view;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.cache.TemplateLoader;

public class DefaultFreeMarkerConfigurer extends FreeMarkerConfigurer {

    /**
     * 重写templateloader的方法,加入html自动转义功能
     */
    protected TemplateLoader getTemplateLoaderForPath(String templateLoaderPath) {
        TemplateLoader loader = super.getTemplateLoaderForPath(templateLoaderPath);
        return new HTMLEscapeTemplateLoader(loader);
    }
}
