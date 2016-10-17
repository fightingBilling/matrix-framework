package org.matrix.framework.core.collection.converter;

import java.util.Date;

import org.matrix.framework.core.collection.TypeConversionException;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.Convert;
import org.slf4j.Logger;

/**
 * 日期转换类
 */
public class DateConverter {

    private final Logger logger = LoggerFactory.getLogger(DateConverter.class);

    public Date fromString(String property) {
        return parseDateTime(property);
    }

    public String toString(Date value) {
        return Convert.toString(value, "yyyy-MM-dd HH:mm:ss");
    }

    private Date parseDateTime(String textValue) {
        Date date = Convert.toDateTime(textValue, null);
        if (date == null)
            date = Convert.toDate(textValue, null);
        if (date == null) {
            this.logger.debug("Can not convert to date, text={}", textValue);
            throw new TypeConversionException("can not convert to date, text=" + textValue);
        }
        return date;
    }
}
