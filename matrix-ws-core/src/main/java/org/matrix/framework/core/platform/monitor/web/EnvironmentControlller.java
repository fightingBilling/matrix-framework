package org.matrix.framework.core.platform.monitor.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.platform.monitor.Pass;
import org.matrix.framework.core.platform.monitor.Sanitizer;
import org.matrix.framework.core.platform.monitor.web.view.Health;
import org.matrix.framework.core.platform.monitor.web.view.Status;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 环境信息监控.
 * @author pankai
 * Nov 5, 2015
 */
@Controller
public class EnvironmentControlller extends MatrixRestController {

    private Environment env;

    @RequestMapping(value = "/monitor/env", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Pass
    public Health envView(HttpServletRequest request) {
        Health.Builder builder = new Health.Builder(Status.UP, getProperties());
        return builder.build();
    }

    private Map<String, Object> getProperties() {
        Sanitizer sanitizer = new Sanitizer();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        //profiles用来声明某些bean与特定container一起时是否加载.
        result.put("profiles", env.getActiveProfiles());
        for (Entry<String, PropertySource<?>> entry : getPropertySources().entrySet()) {
            PropertySource<?> source = entry.getValue();
            String sourceName = entry.getKey();
            if (source instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                for (String name : enumerable.getPropertyNames()) {
                    map.put(name, sanitizer.sanitize(name, enumerable.getProperty(name)));
                }
                result.put(sourceName, map);
            }
        }
        return result;
    }

    private Map<String, PropertySource<?>> getPropertySources() {
        Map<String, PropertySource<?>> map = new LinkedHashMap<String, PropertySource<?>>();
        MutablePropertySources sources = null;
        if (null != env && env instanceof ConfigurableEnvironment) {
            sources = ((ConfigurableEnvironment) env).getPropertySources();
        } else {
            sources = new StandardEnvironment().getPropertySources();
        }
        for (PropertySource<?> source : sources) {
            extract("", map, source);
        }
        return map;
    }

    private void extract(String root, Map<String, PropertySource<?>> map, PropertySource<?> source) {
        if (source instanceof CompositePropertySource) {
            for (PropertySource<?> nest : ((CompositePropertySource) source).getPropertySources()) {
                extract(source.getName() + ":", map, nest);
            }
        } else {
            map.put(root + source.getName(), source);
        }
    }

    @Inject
    public void setEnv(Environment env) {
        this.env = env;
    }

}
