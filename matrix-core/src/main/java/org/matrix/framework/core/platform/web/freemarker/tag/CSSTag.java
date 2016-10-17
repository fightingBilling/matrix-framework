package org.matrix.framework.core.platform.web.freemarker.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.settings.CDNSettings;
import org.matrix.framework.core.settings.DeploymentSettings;
import org.matrix.framework.core.settings.SiteSettings;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class CSSTag extends CDNTagSupport implements TemplateDirectiveModel {
    public static final String TEMPLATE_CSS_TAG = "<link type=\"text/css\" rel=\"stylesheet\" href=\"%s\"%s/>";

    public CSSTag(HttpServletRequest request, SiteSettings siteSettings, DeploymentSettings deploymentSettings, CDNSettings cdnSettings) {
        super(request, siteSettings, deploymentSettings, cdnSettings);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        assertNoBody(body);
        String tags = buildCSSTags(params);
        Writer output = env.getOut();
        output.write(tags);
    }

    String buildCSSTags(Map<String, Object> params) throws IOException, TemplateModelException {
        return buildMultipleResourceTags("href", siteSettings.getCssDir(), TEMPLATE_CSS_TAG, params);
    }
}
