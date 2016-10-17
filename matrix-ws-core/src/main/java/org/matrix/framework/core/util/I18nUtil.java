package org.matrix.framework.core.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;

public class I18nUtil {

    @Autowired
    @Qualifier("messageSource")
    private ResourceBundleMessageSource messageSource;
    private Locale locale;

    public String getMessage(String code) {
        return this.messageSource.getMessage(code, null, Locale.CHINESE);
    }

    public String getMessage(String code, Object[] obj) {
        return this.messageSource.getMessage(code, obj, Locale.CHINESE);
    }

    public String message(String key, String args) {
        return String.format(getMessage(key), new Object[] { args });
    }

    public String getMessage(String code, Locale locale) {
        return this.messageSource.getMessage(code, null, locale);
    }

    public String getMessage(String code, Object[] obj, Locale locale) {
        return this.messageSource.getMessage(code, obj, locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }
}