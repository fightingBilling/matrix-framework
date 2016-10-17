package org.matrix.framework.core.log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

public abstract class LogMessageFilter {

    private final Logger logger = LoggerFactory.getLogger(LogMessageFilter.class);

    private final ConcurrentMap<String, Pattern> patternCache = new ConcurrentHashMap<String, Pattern>();

    protected String mask(String message, String patternExpression) {
        Pattern pattern = null;
        try {
            pattern = getPattern(patternExpression);
            Matcher matcher = pattern.matcher(message);
            StringBuilder builder = filterMessage(message, matcher);
            if (builder.length() > 0)
                return builder.toString();
            return maskEntireMessage(patternExpression);
        } catch (Exception e) {
            this.logger.error(new StringBuilder().append("Failed to parse pattern, pattern = ").append(patternExpression).toString(), e);
        }
        return message;
    }

    private StringBuilder filterMessage(String message, Matcher matcher) {
        StringBuilder builder = new StringBuilder();
        int current = 0;
        int end = 0;
        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            for (int i = 0; i < groupCount; i++) {
                int start = matcher.start(i);
                end = matcher.end(i);
                builder.append(message.subSequence(current, start)).append("--masked--");
                current = end;
            }
        }
        if (end < message.length())
            builder.append(message.substring(end));
        return builder;
    }

    private Pattern getPattern(String patternExpression) {
        Pattern pattern = (Pattern) this.patternCache.get(patternExpression);
        if (null == pattern) {
            pattern = Pattern.compile(patternExpression);
            this.patternCache.putIfAbsent(patternExpression, pattern);
        }
        return pattern;
    }

    private String maskEntireMessage(String pattern) {
        return new StringBuilder().append("Pattern or group not found, entire message masked, Pattern=").append(pattern).toString();
    }

    public abstract String filter(String paramString1, String paramString2);
}
