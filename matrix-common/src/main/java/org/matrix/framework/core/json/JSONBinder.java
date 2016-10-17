package org.matrix.framework.core.json;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.matrix.framework.core.util.RuntimeIOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * jackson2.x
 */
public final class JSONBinder<T> {
    private final Class<T> beanClass;
    private final Class<?>[] elementClasses;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> JSONBinder<T> binder(Class<T> beanClass, Class<?>... elementClasses) {
        return new JSONBinder(beanClass, elementClasses);
    }

    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(dateFormat);
        // 使jackson可作用于JAXB
        // annotations.替代了默认的org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector,但同时致使某些注解失效.
        // mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
        return mapper;
    }

    private JSONBinder(Class<T> beanClass, Class<?>... elementClasses) {
        this.beanClass = beanClass;
        this.elementClasses = elementClasses;
    }

    public T fromJSON(String json) {
        try {
            if ((this.elementClasses == null) || (this.elementClasses.length == 0)) {
                ObjectMapper mapper = createMapper();
                return mapper.readValue(json, this.beanClass);
            } else {
                return fromJSONToGeneric(json);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private T fromJSONToGeneric(String json) throws IOException {
        ObjectMapper mapper = createMapper();
        return mapper.readValue(json, mapper.getTypeFactory().constructParametrizedType(this.beanClass, this.beanClass,
                this.elementClasses));
    }

    public String toJSON(T object) {
        ObjectMapper mapper = createMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
