package org.matrix.framework.core.database.redis.monitor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Inject;

import org.matrix.framework.core.platform.SpringObjectFactory;
import org.matrix.framework.core.platform.monitor.web.view.Health;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * redis监控.
 * @author pankai
 * Nov 3, 2015
 */
@Controller
public class RedisHealthController extends MatrixRestController {

    private SpringObjectFactory springObjectFactory;

    @RequestMapping(value = "/monitor/redis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Health doHealthCheck() {
        Health.Builder builder = new Health.Builder();
        Map<String, RedisConnectionFactory> map = springObjectFactory.getBeansOfType(RedisConnectionFactory.class);
        if (CollectionUtils.isEmpty(map)) {
            return builder.down().withDetail("redis", "unknown").build();
        }
        for (Entry<String, RedisConnectionFactory> entry : map.entrySet()) {
            RedisConnection connection = RedisConnectionUtils.getConnection(entry.getValue());
            try {
                Properties info = connection.info();
                builder.up().withDetail(entry.getKey() + "-version", info.getProperty("redis_version"));
            } finally {
                RedisConnectionUtils.releaseConnection(connection, entry.getValue());
            }
        }
        return builder.build();
    }

    @Inject
    public void setSpringObjectFactory(SpringObjectFactory springObjectFactory) {
        this.springObjectFactory = springObjectFactory;
    }

}
