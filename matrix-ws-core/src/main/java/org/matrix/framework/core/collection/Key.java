package org.matrix.framework.core.collection;

import java.util.Date;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.StringUtils;
import org.slf4j.Logger;

public class Key<T> {

    private final Logger logger = LoggerFactory.getLogger(Key.class);
    private final String name;
    private final Class<? extends T> targetClass;

    public static <T> Key<T> key(String name, Class<T> targetClass) {
        return new Key<T>(name, targetClass);
    }

    public static Key<Integer> intKey(String name) {
        return key(name, Integer.class);
    }

    public static Key<Long> longKey(String name) {
        return key(name, Long.class);
    }

    public static Key<Double> doubleKey(String name) {
        return key(name, Double.class);
    }

    public static Key<String> stringKey(String name) {
        return key(name, String.class);
    }

    public static Key<Date> dateKey(String name) {
        return key(name, Date.class);
    }

    public static Key<Boolean> booleanKey(String name) {
        return key(name, Boolean.class);
    }

    private Key(String name, Class<? extends T> targetClass) {
        if (!StringUtils.hasText(name)) {
            this.logger.debug("name cannot be empty");
            throw new IllegalArgumentException("name cannot be empty");
        }
        if (targetClass == null) {
            this.logger.debug("targetClass cannot be null");
            throw new IllegalArgumentException("targetClass cannot be null");
        }
        if (targetClass.isPrimitive()) {
            this.logger
                    .debug("targetClass cannot be primitive, use wrapper class instead, e.g. Integer.class for int.class");
            throw new IllegalArgumentException(
                    "targetClass cannot be primitive, use wrapper class instead, e.g. Integer.class for int.class");
        }
        this.name = name;
        this.targetClass = targetClass;
    }

    public String name() {
        return this.name;
    }

    public Class<? extends T> targetClass() {
        return this.targetClass;
    }

    public String toString() {
        return String.format("[name=%s, class=%s]", new Object[] { this.name, this.targetClass.getName() });
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Key))
            return false;
        Key<?> that = (Key<?>) o;
        return (this.name.equals(that.name)) && (this.targetClass.equals(that.targetClass));
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.targetClass.hashCode();
        return result;
    }

}
