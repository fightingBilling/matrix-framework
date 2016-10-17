package org.matrix.framework.core.platform.context;

import java.io.IOException;
import java.util.Properties;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.RuntimeIOException;
import org.slf4j.Logger;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

public class PropertyContext extends PropertySourcesPlaceholderConfigurer {
    private final Logger logger = LoggerFactory.getLogger(PropertyContext.class);

    public Properties getAllProperties() {
        try {
            return mergeProperties();
        } catch (IOException e) {
            this.logger.error(e.getMessage());
            throw new RuntimeIOException(e);
        }
    }
}