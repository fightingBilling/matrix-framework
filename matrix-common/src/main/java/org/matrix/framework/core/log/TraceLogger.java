package org.matrix.framework.core.log;

import java.util.concurrent.ConcurrentHashMap;

import org.matrix.framework.core.settings.LogSettings;
import org.matrix.framework.core.util.Convert;
import org.slf4j.MDC;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class TraceLogger {

    private final ConcurrentHashMap<Long, LoggingEventProcessor> processors = new ConcurrentHashMap<Long, LoggingEventProcessor>();
    private PatternLayout layout;
    private String logFolder;
    private static final TraceLogger INSTRANCE = new TraceLogger();

    public static TraceLogger get() {
        return INSTRANCE;
    }

    public void initialize() {
        MDC.clear();
        long threadId = getTargetThreadId();
        this.processors.put(Long.valueOf(threadId), new LoggingEventProcessor(this.layout, this.logFolder));
    }

    private long getTargetThreadId() {
        String targetThreadId = MDC.get("MDC_TARGET_THREAD_ID");
        return Convert.toLong(targetThreadId, Long.valueOf(Thread.currentThread().getId())).longValue();
    }

    public void setLayout(PatternLayout layout) {
        this.layout = layout;
    }

    public void setLogFolder(String logFolder) {
        this.logFolder = logFolder;
    }

    public void clear() {
        long threadId = getTargetThreadId();
        this.processors.remove(Long.valueOf(threadId));
    }

    public void clearAll() {
        this.processors.clear();
    }

    public void process(ILoggingEvent event) {
        long threadId = getTargetThreadId();
        LoggingEventProcessor processor = (LoggingEventProcessor) this.processors.get(Long.valueOf(threadId));

        if ((LogSettings.get().isEnableTraceLog()) && (processor != null)) {
            processor.process(event);
        }
    }

}
