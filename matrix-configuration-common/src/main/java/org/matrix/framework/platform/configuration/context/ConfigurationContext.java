package org.matrix.framework.platform.configuration.context;

/**
 * 配置上下文
 * @author pankai
 * Jan 12, 2016
 */
public class ConfigurationContext {

    /**
     * 组织名称
     */
    private String organizationName;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 可配置的属性
     */
    private String configurableAttribute;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getConfigurableAttribute() {
        return configurableAttribute;
    }

    public void setConfigurableAttribute(String configurableAttribute) {
        this.configurableAttribute = configurableAttribute;
    }

}
