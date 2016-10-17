package org.matrix.framework.core.collection.converter;

import org.matrix.framework.core.xml.XMLBinder;

@SuppressWarnings("all")
public class XMLConvert {
    public <T> T fromString(Class<T> targetClass, String value) {
        return XMLBinder.binder(targetClass).fromXML(value);
    }

    public <T> String toString(T value) {
        XMLBinder binder = XMLBinder.binder(value.getClass());
        return binder.toXML(value);
    }
}