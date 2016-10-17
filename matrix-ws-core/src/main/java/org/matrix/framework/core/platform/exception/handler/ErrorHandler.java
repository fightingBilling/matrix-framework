package org.matrix.framework.core.platform.exception.handler;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.InvalidRequestException;
import org.matrix.framework.core.platform.exception.ResourceNotFoundException;
import org.matrix.framework.core.platform.exception.SessionTimeOutException;
import org.slf4j.Logger;

public class ErrorHandler {
    private final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    public void handle(Throwable e) {
        if (e != null)
            log(e);
    }

    private void log(Throwable e) {
        if (((e instanceof SessionTimeOutException)) || ((e instanceof InvalidRequestException)) || ((e instanceof ResourceNotFoundException)))
            this.logger.info(e.getMessage(), e);
        else
            this.logger.error(e.getMessage(), e);
    }
}