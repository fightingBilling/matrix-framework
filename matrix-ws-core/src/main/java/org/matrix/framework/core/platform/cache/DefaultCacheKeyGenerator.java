package org.matrix.framework.core.platform.cache;

import java.lang.reflect.Method;
import java.util.Date;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.Convert;
import org.matrix.framework.core.util.DigestUtils;
import org.slf4j.Logger;
import org.springframework.cache.interceptor.KeyGenerator;

/**
 * 为目标方法产生缓存键值的生产器
 * 
 * @author pankai 2015年6月2日
 */
public class DefaultCacheKeyGenerator implements KeyGenerator {

    private final Logger logger = LoggerFactory.getLogger(DefaultCacheKeyGenerator.class);

    private static final String NO_PARAM_KEY = "";

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params.length == 0) {
            return NO_PARAM_KEY;
        }
        String key = buildStringCacheKey(method, params);
        this.logger.debug("cache key, method={}, key={}", method, key);
        return encodeKey(key);
    }

    public String encodeKey(String key) {
        // for long key or illegal key, use md5 to short
        if ((key.length() > 32) || (containsIllegalKeyChar(key))) {
            return DigestUtils.md5(key);
        }
        return key;
    }

    private String buildStringCacheKey(Method method, Object[] params) {
        if (params.length == 1) {
            return getKeyValue(params[0]);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(method.getName()).append(':');
        int index = 0;
        for (Object param : params) {
            if (index > 0)
                builder.append(':');
            String value = getKeyValue(param);
            builder.append(value);
            index++;
        }
        return builder.toString();
    }

    private String getKeyValue(Object param) {
        if ((param instanceof CacheKeyGenerator))
            return ((CacheKeyGenerator) param).buildCacheKey();
        if ((param instanceof Enum))
            return ((Enum<?>) param).name();
        if ((param instanceof Date))
            return Convert.toString((Date) param, Convert.DATE_FORMAT_DATETIME);
        return String.valueOf(param);
    }

    private boolean containsIllegalKeyChar(String value) {
        return value.contains(" ");
    }
}
