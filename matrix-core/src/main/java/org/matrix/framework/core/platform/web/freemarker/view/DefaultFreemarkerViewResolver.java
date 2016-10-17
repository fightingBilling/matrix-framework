package org.matrix.framework.core.platform.web.freemarker.view;

import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

public class DefaultFreemarkerViewResolver extends FreeMarkerViewResolver {

    public String buildFullTemplatePath(String template) {
        return String.format("%s%s%s", new Object[] { getPrefix(), template, getSuffix() });
    }

}
