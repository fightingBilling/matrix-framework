package org.matrix.framework.core.settings;

public class DeploymentSettings {

    private String host;
    private String deploymentContext;
    private int httpPort = 80;
    private int httpsPort = 443;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDeploymentContext() {
        return deploymentContext;
    }

    public void setDeploymentContext(String deploymentContext) {
        this.deploymentContext = deploymentContext;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getHttpsPort() {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

}
