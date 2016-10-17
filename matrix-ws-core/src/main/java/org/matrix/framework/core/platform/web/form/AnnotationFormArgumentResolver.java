package org.matrix.framework.core.platform.web.form;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletRequest;

import org.matrix.framework.core.util.AssertUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.util.WebUtils;

public class AnnotationFormArgumentResolver extends ServletModelAttributeMethodProcessor {
    private final ConcurrentMap<Class<?>, Map<String, String>> definitionsCache = new ConcurrentHashMap<Class<?>, Map<String, String>>();

    public AnnotationFormArgumentResolver() {
        super(true);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAnnotationPresent(Form.class);
    }

    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = (ServletRequest) request.getNativeRequest(ServletRequest.class);
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
        bind(servletRequest, servletBinder);
    }

    private void bind(ServletRequest request, ServletRequestDataBinder binder) {
        Map<String, ?> values = parsePropertyValues(request, binder.getTarget().getClass());
        MutablePropertyValues propertyValues = new MutablePropertyValues(values);
        MultipartRequest multipartRequest = (MultipartRequest) WebUtils.getNativeRequest(request, MultipartRequest.class);
        if (multipartRequest != null) {
            bindMultipart(multipartRequest.getMultiFileMap(), propertyValues);
        }

        binder.bind(propertyValues);
    }

    private Map<String, ?> parsePropertyValues(ServletRequest request, Class<?> targetClass) {
        Map<String, Object> params = new HashMap<String, Object>();
        AssertUtils.assertNotNull(request, "request must not be null");
        Enumeration<String> paramNames = request.getParameterNames();
        Map<String, String> parameterMappings = getOrCreateParameterMappings(targetClass);
        if (paramNames != null)
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String fieldName = (String) parameterMappings.get(paramName);
                if (fieldName == null) {
                    fieldName = paramName;
                }
                Object value = getParamValue(request, paramName);
                params.put(fieldName, value);
            }
        return params;
    }

    private Object getParamValue(ServletRequest request, String paramName) {
        String[] value = request.getParameterValues(paramName);
        if (value == null)
            return null;
        if (value.length > 1)
            return value;
        return value[0];
    }

    Map<String, String> getOrCreateParameterMappings(Class<?> targetClass) {
        Map<String, String> existingMappings = (Map<String, String>) this.definitionsCache.get(targetClass);
        if (existingMappings == null) {
            Map<String, String> mappings = createParameterMappings(targetClass);
            this.definitionsCache.putIfAbsent(targetClass, mappings);
            return mappings;
        }
        return existingMappings;
    }

    private Map<String, String> createParameterMappings(Class<?> targetClass) {
        Field[] fields = targetClass.getDeclaredFields();
        Map<String, String> mappings = new HashMap<String, String>(fields.length);

        for (Field field : fields) {
            FormParam annotation = (FormParam) field.getAnnotation(FormParam.class);
            if ((annotation != null) && (!annotation.value().isEmpty())) {
                mappings.put(annotation.value(), field.getName());
            }
        }
        return mappings;
    }

    private void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues propertyValues) {
        for (Map.Entry<String, List<MultipartFile>> entry : multipartFiles.entrySet()) {
            String key = (String) entry.getKey();
            List<MultipartFile> values = (List<MultipartFile>) entry.getValue();
            if (values.size() == 1) {
                MultipartFile value = (MultipartFile) values.get(0);
                if (!value.isEmpty())
                    propertyValues.add(key, value);
            } else {
                propertyValues.add(key, values);
            }
        }
    }
}