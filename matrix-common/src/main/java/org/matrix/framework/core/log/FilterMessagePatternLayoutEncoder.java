package org.matrix.framework.core.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

public class FilterMessagePatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {

    public void start() {
        FilterMessagePatternLayout patternLayout = new FilterMessagePatternLayout();
        patternLayout.setContext(this.context);
        patternLayout.setPattern(getPattern());
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

}
