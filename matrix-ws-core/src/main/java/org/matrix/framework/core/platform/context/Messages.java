package org.matrix.framework.core.platform.context;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class Messages extends ReloadableResourceBundleMessageSource {
    public String getMessage(String key, Object[] arguments) {
        return super.getMessage(key, arguments, Locale.getDefault());
    }
}