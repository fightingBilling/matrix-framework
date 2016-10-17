package org.matrix.framework.core.platform.web.freemarker.view;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.platform.web.cookie.CookieContext;
import org.matrix.framework.core.platform.web.freemarker.tag.CSSTag;
import org.matrix.framework.core.platform.web.freemarker.tag.MasterTag;
import org.matrix.framework.core.platform.web.freemarker.tag.MasterTemplateLoader;
import org.matrix.framework.core.platform.web.freemarker.tag.TagNames;
import org.matrix.framework.core.platform.web.request.RequestContext;
import org.matrix.framework.core.platform.web.session.SessionContext;
import org.matrix.framework.core.settings.CDNSettings;
import org.matrix.framework.core.settings.DeploymentSettings;
import org.matrix.framework.core.settings.SiteSettings;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.template.Template;
import freemarker.template.TemplateModelException;

public class DefaultFreemarkerView extends FreeMarkerView {

    protected SiteSettings siteSettings;
    protected DeploymentSettings deploymentSettings;
    protected CDNSettings cdnSettings;
    protected DefaultFreemarkerViewResolver viewResolver;
    protected RequestContext requestContext;
    protected SessionContext sessionContext;
    protected CookieContext cookieContext;
    //protected I18nUtil i18nUtil;

    /**
     * DefaultFreemarkerView初始化之后从applicationContext中取得相应的bean,保持其引用.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        siteSettings = getBeanFromApplicationContext(SiteSettings.class);
        deploymentSettings = getBeanFromApplicationContext(DeploymentSettings.class);
        viewResolver = getBeanFromApplicationContext(DefaultFreemarkerViewResolver.class);
        cdnSettings = getBeanFromApplicationContext(CDNSettings.class);
        requestContext = getBeanFromApplicationContext(RequestContext.class);
        sessionContext = getApplicationContext().getBean("sessionContext", SessionContext.class);
        cookieContext = getBeanFromApplicationContext(CookieContext.class);
        //i18nUtil = getBeanFromApplicationContext(I18nUtil.class);
    }

    protected <T> T getBeanFromApplicationContext(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    @Override
    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
        registerMasterTag(model, request);
        registerCSSTag(model, request);
        exposeRequestContext(model);
    }

    private void exposeRequestContext(Map<String, Object> model) throws TemplateModelException {
        Object previousValue = model.put("requestContext", requestContext);
        if (previousValue != null)
            throw new TemplateModelException("requestContext is reserved name in model, please use different name in model");
    }

    protected void assertTagNameIsAvailable(Object previousValue, String tagName) throws TemplateModelException {
        if (previousValue != null)
            throw new TemplateModelException(String.format("%1$s is reserved name in model as @%1$s, please use different name in model", tagName));
    }

    private void registerMasterTag(Map<String, Object> model, HttpServletRequest request) throws TemplateModelException {
        Locale locale = RequestContextUtils.getLocale(request);
        MasterTemplateLoader templateLoader = new MasterTemplateLoader(viewResolver, this, locale);
        Object previousValue = model.put(TagNames.TAG_MASTER, new MasterTag(model, templateLoader));
        assertTagNameIsAvailable(previousValue, TagNames.TAG_MASTER);
    }

    private void registerCSSTag(Map<String, Object> model, HttpServletRequest request) throws TemplateModelException {
        Object previousValue = model.put(TagNames.TAG_CSS, new CSSTag(request, siteSettings, deploymentSettings, cdnSettings));
        assertTagNameIsAvailable(previousValue, TagNames.TAG_CSS);
    }

    public Template loadTemplate(String fullTemplatePath, Locale locale) throws IOException {
        return getTemplate(fullTemplatePath, locale);
    }

}
