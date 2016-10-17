package org.matrix.framework.core.log;

import java.util.Map;

import ch.qos.logback.classic.PatternLayout;

public class FilterMessagePatternLayout extends PatternLayout {

    public FilterMessagePatternLayout() {
        Map<String, String> converts = getInstanceConverterMap();
        converts.put("m", FilterMessageConvert.class.getName());
        converts.put("msg", FilterMessageConvert.class.getName());
        converts.put("message", FilterMessageConvert.class.getName());
    }

}
