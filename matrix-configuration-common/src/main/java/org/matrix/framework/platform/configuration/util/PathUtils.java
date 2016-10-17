package org.matrix.framework.platform.configuration.util;

import org.apache.commons.lang3.StringUtils;
import org.matrix.framework.platform.configuration.context.ConfigurationContext;

public class PathUtils {

    /**
     * 通过提供的{@link ConfigurationContext}得到zookeeper中的path.
     * @param context
     * @return
     */
    public static String getPath(ConfigurationContext context) {
        if (null == context) {
            throw new NullPointerException("ConfigurationContext is null.");
        }
        if (StringUtils.isBlank(context.getOrganizationName())) {
            throw new IllegalArgumentException("Organization name is blank.");
        }
        if (StringUtils.isBlank(context.getProjectName())) {
            throw new IllegalArgumentException("Project name is blank.");
        }
        if (StringUtils.isBlank(context.getConfigurableAttribute())) {
            throw new IllegalArgumentException("Configurable attribute is blank.");
        }
        return new StringBuilder().append("/").append(context.getOrganizationName()).append("/").append(context.getProjectName()).append("/configuration/").append(context.getConfigurableAttribute())
                .toString();
    }

}
