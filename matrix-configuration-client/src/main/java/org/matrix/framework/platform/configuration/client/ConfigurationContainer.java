package org.matrix.framework.platform.configuration.client;

import java.util.HashMap;
import java.util.Map;

import org.matrix.framework.platform.configuration.context.ConfigurationContext;

/**
 * 配置项容器
 * @author pankai
 * 2016年1月13日
 */
public class ConfigurationContainer {

    private Map<ConfigurationContext, ConfigurationCallback> map = new HashMap<ConfigurationContext, ConfigurationCallback>();

    public void add(ConfigurationContext context, ConfigurationCallback callback) {
        map.put(context, callback);
    }

    public Map<ConfigurationContext, ConfigurationCallback> getConfigurations() {
        return map;
    }
}
