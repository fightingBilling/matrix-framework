package org.matrix.framework.core.platform.web.freemarker.tag;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class BodyTag implements TemplateDirectiveModel {
    private final TemplateDirectiveBody body;

    public BodyTag(TemplateDirectiveBody body) {
        this.body = body;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        this.body.render(env.getOut());
    }

}
