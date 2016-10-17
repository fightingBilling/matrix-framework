package org.matrix.framework.core.platform.monitor.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.matrix.framework.core.platform.SpringObjectFactory;
import org.matrix.framework.core.platform.monitor.Pass;
import org.matrix.framework.core.platform.monitor.web.view.Health;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 数据源监控.
 * @author pankai
 * Nov 3, 2015
 */
@Controller
public class DataSourceHealthController extends MatrixRestController {

    private static final Map<String, String> PRODUCT_SPECIFIC_QUERIES;

    static {
        Map<String, String> queries = new HashMap<String, String>();
        queries.put("HSQL Database Engine", "SELECT COUNT(*) FROM " + "INFORMATION_SCHEMA.SYSTEM_USERS");
        queries.put("Oracle", "SELECT 'Hello' from DUAL");
        queries.put("Apache Derby", "SELECT 1 FROM SYSIBM.SYSDUMMY1");
        PRODUCT_SPECIFIC_QUERIES = Collections.unmodifiableMap(queries);
    }

    private static final String DEFAULT_QUERY = "SELECT 1";

    private SpringObjectFactory springObjectFactory;

    @RequestMapping(value = "/monitor/datasource", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Pass
    public Health doHealthCheck(HttpServletRequest request) {
        Health.Builder builder = new Health.Builder();
        Map<String, DataSource> map = springObjectFactory.getBeansOfType(DataSource.class);
        if (CollectionUtils.isEmpty(map)) {
            return builder.down().withDetail("database", "unknown").build();
        }
        for (Entry<String, DataSource> entry : map.entrySet()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(entry.getValue());
            String product = getProduct(jdbcTemplate);
            builder.up().withDetail("database:" + entry.getKey(), product);
            String validationQuery = getValidationQuery(product);
            if (StringUtils.hasText(validationQuery)) {
                try {
                    List<Object> results = jdbcTemplate.query(validationQuery, new SingleColumnRowMapper<Object>());
                    Object result = DataAccessUtils.requiredSingleResult(results);
                    builder.withDetail("hello:" + entry.getKey(), result);
                } catch (Exception e) {
                    //有任一数据源出现问题立即上报.
                    return builder.down(e).build();
                }
            }
        }
        return builder.build();
    }

    private String getValidationQuery(String product) {
        String query = PRODUCT_SPECIFIC_QUERIES.get(product);
        if (!StringUtils.hasText(query)) {
            query = DEFAULT_QUERY;
        }
        return query;
    }

    private String getProduct(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.execute(new ConnectionCallback<String>() {

            @Override
            public String doInConnection(Connection con) throws SQLException, DataAccessException {
                return con.getMetaData().getDatabaseProductName();
            }
        });
    }

    @Inject
    public void setSpringObjectFactory(SpringObjectFactory springObjectFactory) {
        this.springObjectFactory = springObjectFactory;
    }

}
