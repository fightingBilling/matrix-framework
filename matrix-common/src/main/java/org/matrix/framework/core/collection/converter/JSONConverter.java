package org.matrix.framework.core.collection.converter;

import org.matrix.framework.core.json.JSONBinder;

public class JSONConverter {
    public <T> T fromString(Class<T> targetClass, String value, Class<?>... elementClasses) {
        return JSONBinder.binder(targetClass, elementClasses).fromJSON(value);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> String toString(T value, Class<?>... elementClasses) {
        JSONBinder jsonBinder = JSONBinder.binder(value.getClass(), elementClasses);
        return jsonBinder.toJSON(value);
    }
}
