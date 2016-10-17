package org.matrix.framework.core.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.matrix.framework.core.util.ExceptionUtils;
import org.slf4j.Logger;

@SuppressWarnings("all")
public enum LoggerFactory {

    JOBLOGGER("JOBLOGGER"), //
    MONITORLOGGER("MONITORLOGGER"), //
    SQLLOGGER("SQLLOGGER"), //
    HQLLOGGER("HQLLOGGER"), //
    CQLLOGGER("CQLLOGGER"), //
    JMSLOGGER("JMSLOGGER"), //
    DEBUGLOGGER("DEBUGLOGGER"), //
    REDISLOGGER("REDISLOGGER"), //
    MEMCACHEDLOGGER("MEMCACHEDLOGGER");

    private final Logger logger;
    private static final Map<Class, Logger> LOGGERMAP = new ConcurrentHashMap<Class, Logger>();

    private LoggerFactory(String loggerName) {
        this.logger = org.slf4j.LoggerFactory.getLogger(loggerName);
    }

    public static Logger getLogger(Class clazz) {
        Logger logger = LOGGERMAP.get(clazz);
        if (null == logger) {
            logger = org.slf4j.LoggerFactory.getLogger(clazz);
        }
        return logger;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public static void trace(Class clazz, Exception e) {
        getLogger(clazz).error(ExceptionUtils.stackTrace(e));
    }
}
