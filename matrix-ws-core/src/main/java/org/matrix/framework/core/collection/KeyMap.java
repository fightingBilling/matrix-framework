package org.matrix.framework.core.collection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.RuntimeIOException;
import org.slf4j.Logger;

/**
 * 基于HashMap的自定义Map,包含简单的"序列化"与"反序列化"
 * 
 * @author pankai 2015年6月18日
 */
public class KeyMap {

    private final Logger logger = LoggerFactory.getLogger(KeyMap.class);

    private final TypeConverter typeConverter = new TypeConverter();

    private final Map<String, Object> context = new HashMap<String, Object>();

    public <T> void put(Key<T> key, T value) {
        this.context.put(key.name(), value);
    }

    public <T> T get(Key<T> key) {
        return get(key.name(), key.targetClass());
    }

    public <T> String getString(Key<T> key) {
        Object value = this.context.get(key.name());
        if (value == null)
            return null;
        return this.typeConverter.toString(value);
    }

    public void putString(String key, String value) {
        this.context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(String key, Class<T> expectedClass) {
        Object value = this.context.get(key);

        if (value == null)
            return null;

        if (expectedClass.isAssignableFrom(value.getClass()))
            return (T) value;

        if (!(value instanceof String)) {
            this.logger.debug("class does not match, targetClass={}, expectedClass={}", value.getClass().getName(),
                    expectedClass.getName());
            throw new TypeConversionException(new StringBuilder().append("class does not match, targetClass=").append(
                    value.getClass().getName()).append(", expectedClass=").append(expectedClass.getName()).toString());
        }
        return this.typeConverter.fromString((String) value, expectedClass);
    }

    public Map<String, String> getTextValues() {
        Map<String, String> values = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : this.context.entrySet()) {
            values.put(entry.getKey(), this.typeConverter.toString(entry.getValue()));
        }
        return values;
    }

    public void putAll(KeyMap map) {
        this.context.putAll(map.context);
    }

    public void putAll(Map<String, ?> properties) {
        this.context.putAll(properties);
    }

    public void clear() {
        this.context.clear();
    }

    public Map<String, Object> getAll() {
        return this.context;
    }

    public int size() {
        return this.context.size();
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : this.context.entrySet()) {
            String key = (String) entry.getKey();
            String value = this.typeConverter.toString(entry.getValue());
            builder.append(key).append("=").append(value).append("\n");
        }
        return builder.toString();
    }

    public void deserialize(String text) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(text));
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                // ASCII码61对应的字符为"="
                int index = line.indexOf(61);
                if (index <= 0) {
                    this.logger.debug("can not parse line, line={}", line);
                    throw new TypeConversionException(new StringBuilder().append("can not parse line, line=").append(
                            line).toString());
                }
                String key = line.substring(0, index);
                String value = index == line.length() - 1 ? null : line.substring(index + 1);
                this.context.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
