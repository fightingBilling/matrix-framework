package org.matrix.framework.core.platform.web.freemarker.tag;

import java.util.Map;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateModelException;

public class TagSupport {

    protected void assertNoBody(TemplateDirectiveBody body) throws TemplateModelException {
        if (body != null)
            throw new TemplateModelException(String.format("%s directive does not allow body", getClass().getSimpleName()));
    }

    protected void assertHasBody(TemplateDirectiveBody body) throws TemplateModelException {
        if (body == null)
            throw new TemplateModelException(String.format("%s directive should have body", getClass().getSimpleName()));
    }

    protected String getRequiredStringParam(Map<String, Object> params, String key) throws TemplateModelException {
        Object value = params.get(key);
        if (!(value instanceof SimpleScalar))
            throw new TemplateModelException(String.format("%s param is required by %s, and must be string", key, getClass().getSimpleName()));
        return ((SimpleScalar) value).getAsString();
    }

    protected String getStringParam(Map<Object, Object> params, String key) throws TemplateModelException {
        Object value = params.get(key);
        if (value == null)
            return null;
        if (!(value instanceof SimpleScalar))
            throw new TemplateModelException(String.format("%s param must be string in %s", key, getClass().getSimpleName()));
        return ((SimpleScalar) value).getAsString();
    }
}
