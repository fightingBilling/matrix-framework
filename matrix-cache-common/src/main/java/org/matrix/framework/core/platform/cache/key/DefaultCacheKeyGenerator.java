package org.matrix.framework.core.platform.cache.key;

import java.lang.reflect.Method;
import java.util.Date;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.Convert;
import org.matrix.framework.core.util.DigestUtils;
import org.slf4j.Logger;
import org.springframework.cache.interceptor.KeyGenerator;

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

    /**
     * 根据方法及参数来生成cache key.
     */
    private String buildStringCacheKey(Method method, Object[] params) {
        //只有一个参数的时候使用该参数来生成cache key.
        if (params.length == 1) {
            return getKeyValue(params[0]);
        }
        StringBuilder builder = new StringBuilder();
        //多个参数使用方法名和参数用:间隔的形式来生成cache key.
        builder.append(method.getName()).append(':');
        int index = 0;
        for (Object param : params) {
            //如果参数中存在指定的CacheKeyGenerator,则使用其中定义的方法生成key.
            if (param instanceof CacheKeyGenerator) {
                return encodeKey(((CacheKeyGenerator) param).buildCacheKey());
            }
            if (index > 0)
                builder.append(':');
            String value = getKeyValue(param);
            builder.append(value);
            index++;
        }
        return builder.toString();
    }

    private String getKeyValue(Object param) {
        //if (CacheKeyGenerator.class.isAssignableFrom(param.getClass())) {
        if (param instanceof CacheKeyGenerator) {
            return encodeKey(((CacheKeyGenerator) param).buildCacheKey());
        }
        if ((param instanceof Enum))
            return ((Enum<?>) param).name();
        if ((param instanceof Date))
            return Convert.toString((Date) param, Convert.DATE_FORMAT_DATETIME);
        //复杂对象通过实现toString方法也可达到CacheKeyGenerator的效果.
        return String.valueOf(param);
    }

    private boolean containsIllegalKeyChar(String value) {
        return value.contains(" ");
    }
}
