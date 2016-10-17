package org.matrix.framework.core.platform.web.freemarker.tag;

import java.io.IOException;
import java.util.Locale;

import org.matrix.framework.core.platform.web.freemarker.view.DefaultFreemarkerView;
import org.matrix.framework.core.platform.web.freemarker.view.DefaultFreemarkerViewResolver;

import freemarker.template.Template;

public class MasterTemplateLoader {
    private final DefaultFreemarkerViewResolver viewResolver;
    private final DefaultFreemarkerView view;
    private final Locale locale;

    public MasterTemplateLoader(DefaultFreemarkerViewResolver viewResolver, DefaultFreemarkerView view, Locale locale) {
        this.viewResolver = viewResolver;
        this.view = view;
        this.locale = locale;
    }

    public Template loadTemplate(String template) throws IOException {
        String fullTemplatePath = viewResolver.buildFullTemplatePath(template);
        return view.loadTemplate(fullTemplatePath, locale);
    }
}
