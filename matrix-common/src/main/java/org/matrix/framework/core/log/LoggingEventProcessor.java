package org.matrix.framework.core.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.RandomStringUtils;
import org.matrix.framework.core.util.ExceptionUtils;
import org.matrix.framework.core.util.IOUtils;
import org.matrix.framework.core.util.RuntimeIOException;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LoggingEventProcessor {
    @SuppressWarnings("unused")
    private static final int MAX_HOLD_SIZE = 5000;
    private final Queue<ILoggingEvent> events = new ConcurrentLinkedQueue<ILoggingEvent>();

    private boolean hold = true;

    private int eventSize = 0;
    private final PatternLayout layout;
    private final String logFolder;
    private Writer writer;

    public LoggingEventProcessor(PatternLayout layout, String logFolder) {
        this.layout = layout;
        this.logFolder = logFolder;
    }

    public void process(ILoggingEvent event) {
        if (this.hold) {
            event.getThreadName();
            this.events.add(event);
            this.eventSize += 1;
            if (flushLog(event)) {
                this.hold = false;

                flushTraceLogs();

                this.events.clear();
            }
        } else {
            write(event);
        }
    }

    private boolean flushLog(ILoggingEvent event) {
        return (event.getLevel().isGreaterOrEqual(Level.WARN)) || (this.eventSize > 5000);
    }

    public void flushTraceLogs() {
        for (ILoggingEvent logEvent : this.events)
            write(logEvent);
    }

    void write(ILoggingEvent event) {
        try {
            String log = this.layout.doLayout(event);
            if (null == this.writer) {
                this.writer = createWriter();
            }
            this.writer.write(log);
            this.writer.flush();
        } catch (Exception e) {
            System.err.println("Failed to write log, exception = " + ExceptionUtils.stackTrace(e));
            throw new RuntimeIOException(e);
        }
    }

    private Writer createWriter() throws FileNotFoundException {
        if (this.logFolder == null)
            return new BufferedWriter(new OutputStreamWriter(System.err, Charset.forName("UTF-8")));
        File logFile = new File(generateLogFilePath(getAction(), new Date(), getRequestId()));
        createParentFolder(logFile);
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), Charset.forName("UTF-8")));
    }

    private void createParentFolder(File logFile) {
        File folder = logFile.getParentFile();
        if (!folder.exists())
            folder.mkdirs();
    }

    void cleanup(boolean forceFlushTraceLog) {
        if (forceFlushTraceLog) {
            flushTraceLogs();
        }
        if (this.logFolder == null)
            IOUtils.flush(this.writer);
        else
            IOUtils.close(this.writer);
    }

    private String getRequestId() {
        String requestId = MDC.get("MDC_REQUEST_ID");
        if (null == requestId) {
            requestId = "unknow";
        }
        return requestId;
    }

    private String getAction() {
        String action = MDC.get("MDC_ACTION");
        if (null == action) {
            action = "unknow";
        }
        return action;
    }

    String generateLogFilePath(String action, Date targetDate, String requestId) {
        String sequence = RandomStringUtils.randomNumeric(5);
        return String.format("%1$s/%2$tY/%2$tm/%2$td/%3$s/%2$tH%2$tM.%4$s.%5$s.log", logFolder, targetDate, action, requestId, sequence);
    }
}
