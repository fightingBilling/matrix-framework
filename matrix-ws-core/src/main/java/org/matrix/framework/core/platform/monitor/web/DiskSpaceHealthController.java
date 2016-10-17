package org.matrix.framework.core.platform.monitor.web;

import java.io.File;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.monitor.Pass;
import org.matrix.framework.core.platform.monitor.web.view.Health;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 磁盘剩余容量监控
 * @configurable management.health.diskspace MB
 * @author pankai
 * Nov 3, 2015
 */
@Controller
public class DiskSpaceHealthController extends MatrixRestController {

    private static Logger logger = LoggerFactory.getLogger(DiskSpaceHealthController.class);

    private Environment env;

    @RequestMapping(value = "/monitor/disk", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Pass
    public Health doHealthCheck(HttpServletRequest request) {
        Health.Builder builder = new Health.Builder();
        File file = new File(".");
        long diskFreeInBytes = file.getFreeSpace();
        long threshold = env.getProperty("management.health.diskspace", Long.class, 10L) * 1024 * 1024;
        if (diskFreeInBytes >= threshold) {
            builder.up();
        } else {
            logger.warn(String.format("Free disk space below threshold. " + "Available: %d bytes (threshold: %d bytes)", diskFreeInBytes, threshold));
            builder.down();
        }
        builder.withDetail("free", diskFreeInBytes).withDetail("threshold", threshold);
        return builder.build();
    }

    @Inject
    public void setEnv(Environment env) {
        this.env = env;
    }

}
