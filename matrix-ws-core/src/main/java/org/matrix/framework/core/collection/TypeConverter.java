package org.matrix.framework.core.collection;

import java.util.Date;

import org.matrix.framework.core.collection.converter.DateConverter;
import org.matrix.framework.core.collection.converter.NumberConverter;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.StringUtils;
import org.slf4j.Logger;

public class TypeConverter {

    private final Logger logger = LoggerFactory.getLogger(TypeConverter.class);
    static final String ERROR_MESSAGE_TARGET_CLASS_CANNOT_BE_PRIMITIVE = "targetClass cannot be primitive, use wrapper class instead, e.g. Integer.class for int.class";
    final DateConverter dateConverter = new DateConverter();

    final NumberConverter numberConverter = new NumberConverter();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T fromString(String textValue, Class<T> targetClass) {
        if (targetClass.isPrimitive()) {
            this.logger
                    .debug("targetClass cannot be primitive, use wrapper class instead, e.g. Integer.class for int.class");
            throw new IllegalArgumentException(
                    "targetClass cannot be primitive, use wrapper class instead, e.g. Integer.class for int.class");
        }
        if (String.class.equals(targetClass))
            return (T) textValue;
        if (!StringUtils.hasText(textValue))
            return null;

        if (Boolean.class.equals(targetClass))
            return (T) Boolean.valueOf(textValue);

        if (Number.class.isAssignableFrom(targetClass)) {
            return (T) this.numberConverter.convertToNumber(textValue, (Class<? extends Number>) targetClass);
        }
        if (Character.class.equals(targetClass))
            return (T) Character.valueOf(textValue.charAt(0));

        if (Enum.class.isAssignableFrom(targetClass))
            return (T) Enum.valueOf((Class<? extends Enum>) targetClass, textValue);
        if (Date.class.equals(targetClass))
            return (T) this.dateConverter.fromString(textValue);
        this.logger.debug("not supported type, targetClass={}", targetClass.getName());
        throw new TypeConversionException("not supported type, targetClass=" + targetClass.getName());
    }

    public String toString(Object value) {
        if (value == null)
            return "";
        if ((value instanceof String))
            return (String) value;
        if ((value instanceof Boolean))
            return String.valueOf(value);
        if ((value instanceof Number))
            return String.valueOf(value);
        if ((value instanceof Enum))
            return ((Enum<?>) value).name();
        if ((value instanceof Character))
            return String.valueOf(value);
        if ((value instanceof Date))
            return this.dateConverter.toString((Date) value);

        this.logger.debug("not supported type, targetClass={}", value.getClass().getName());

        throw new TypeConversionException("not supported type, targetClass=" + value.getClass().getName());
    }
}
