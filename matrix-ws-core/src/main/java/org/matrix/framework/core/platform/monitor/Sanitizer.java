package org.matrix.framework.core.platform.monitor;

import java.util.regex.Pattern;

import org.springframework.util.Assert;

/**
 * 隐藏敏感信息.关键字可设置.默认会隐藏name中包含password,secret,key的信息.
 * @author pankai
 * Nov 5, 2015
 */
public class Sanitizer {

    private static final String[] REGEX_PARTS = { "*", "$", "^", "+" };

    private Pattern[] keysToSanitize;

    public Sanitizer() {
        setKeysToSanitize(new String[] { "password", "secret", "key" });
    }

    /**
     * Keys that should be sanitized. Keys can be simple strings that the property ends
     * with or regex expressions.
     * @param keysToSanitize the keys to sanitize
     */
    public void setKeysToSanitize(String... keysToSanitize) {
        Assert.notNull(keysToSanitize, "KeysToSanitize must not be null");
        this.keysToSanitize = new Pattern[keysToSanitize.length];
        for (int i = 0; i < keysToSanitize.length; i++) {
            this.keysToSanitize[i] = getPattern(keysToSanitize[i]);
        }
    }

    /**
     * Sanitize the given value if necessary.
     * @param key the key to sanitize
     * @param value the value
     * @return the potentially sanitized value
     */
    public Object sanitize(String key, Object value) {
        for (Pattern pattern : this.keysToSanitize) {
            if (pattern.matcher(key).matches()) {
                return (value == null ? null : "******");
            }
        }
        return value;
    }

    private Pattern getPattern(String value) {
        if (isRegex(value)) {
            return Pattern.compile(value, Pattern.CASE_INSENSITIVE);
        }
        return Pattern.compile(".*" + value + "$", Pattern.CASE_INSENSITIVE);
    }

    private boolean isRegex(String value) {
        for (String part : REGEX_PARTS) {
            if (value.contains(part)) {
                return true;
            }
        }
        return false;
    }

}
