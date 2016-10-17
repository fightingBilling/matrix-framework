package org.matrix.framework.core.log;

import java.util.Iterator;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;

public class TraceAppender extends AppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent> {

    private String logFolder;
    private PatternLayout layout;

    public void start() {
        super.start();
        TraceLogger traceLogger = TraceLogger.get();
        traceLogger.setLayout(this.layout);
        traceLogger.setLogFolder(this.logFolder);
    }

    public void stop() {
        super.stop();
        TraceLogger.get().clearAll();
    }

    protected void append(ILoggingEvent event) {
        TraceLogger.get().process(event);
    }

    public void setLogFolder(String logFolder) {
        this.logFolder = logFolder;
    }

    public void setLayout(PatternLayout layout) {
        this.layout = layout;
    }

    public void addAppender(Appender<ILoggingEvent> appender) {
        throw new IllegalStateException("unexpected flow");
    }

    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        throw new IllegalStateException("unexpected flow");
    }

    public Appender<ILoggingEvent> getAppender(String name) {
        throw new IllegalStateException("unexpected flow");
    }

    public boolean isAttached(Appender<ILoggingEvent> appender) {
        throw new IllegalStateException("unexpected flow");
    }

    public void detachAndStopAllAppenders() {
        throw new IllegalStateException("unexpected flow");
    }

    public boolean detachAppender(Appender<ILoggingEvent> appender) {
        throw new IllegalStateException("unexpected flow");
    }

    public boolean detachAppender(String name) {
        throw new IllegalStateException("unexpected flow");
    }

}
