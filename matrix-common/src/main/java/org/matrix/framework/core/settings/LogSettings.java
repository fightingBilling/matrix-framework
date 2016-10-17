package org.matrix.framework.core.settings;

import org.matrix.framework.core.log.LogMessageFilter;

public class LogSettings {

    private static final LogSettings INSTANCE = new LogSettings();

    private boolean enableTraceLog = true;
    private boolean alwaysWriteTraceLog;
    private LogMessageFilter logMessageFilter;

    public static LogSettings get() {
        return INSTANCE;
    }

    public boolean isEnableTraceLog() {
        return this.enableTraceLog;
    }

    public void setEnableTraceLog(boolean enableTraceLog) {
        this.enableTraceLog = enableTraceLog;
    }

    public boolean isAlwaysWriteTraceLog() {
        return this.alwaysWriteTraceLog;
    }

    public void setAlwaysWriteTraceLog(boolean alwaysWriteTraceLog) {
        this.alwaysWriteTraceLog = alwaysWriteTraceLog;
    }

    public static LogSettings getInstance() {
        return INSTANCE;
    }

    public LogMessageFilter getLogMessageFilter() {
        return this.logMessageFilter;
    }

    public void setLogMessageFilter(LogMessageFilter logMessageFilter) {
        this.logMessageFilter = logMessageFilter;
    }
}
