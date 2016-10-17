package org.matrix.framework.core.platform.web;

import java.util.Locale;

import javax.inject.Inject;

import org.matrix.framework.core.collection.converter.JSONConverter;
import org.matrix.framework.core.platform.context.Messages;
import org.springframework.context.i18n.LocaleContextHolder;

public class DefaultController implements SpringController {
    protected Messages messages;
    protected JSONConverter jsonConverter;

    public <T> T fromString(Class<T> targetClass, String value) {
        return this.jsonConverter.fromString(targetClass, value, new Class[0]);
    }

    protected <T> String toString(T value) {
        return this.jsonConverter.toString(value, new Class[0]);
    }

    protected String getMessage(String messageKey, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messages.getMessage(messageKey, args, locale);
    }

    @Inject
    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    @Inject
    public void setjSONConverter(JSONConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}