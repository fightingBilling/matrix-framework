package org.matrix.framework.core.platform.web.site;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.settings.SiteSettings;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

public class URLBuilder {

    private final Logger logger = LoggerFactory.getLogger(URLBuilder.class);
    String contextPath;
    String deploymentContext;
    String serverName;
    String logicalURL;
    int httpsPort;
    int httpPort;
    SiteSettings siteSettings;

    public String constructRelativeURL(String relativeURL) {
        StringBuilder builder = new StringBuilder();
        buildRelativeURL(builder, relativeURL);
        return builder.toString();
    }

    public String constructAbsoluteURL(String scheme, String relativeURL) {
        if (!relativeURL.startsWith("/")) {
            this.logger.error("relative url must start with '/' to construct absolute url");
            throw new IllegalArgumentException("relative url must start with '/' to construct absolute url");
        }
        StringBuilder builder = new StringBuilder();
        buildURLPrefix(builder, scheme);
        buildRelativeURL(builder, relativeURL);
        return builder.toString();
    }

    public String buildRelativeURL() {
        StringBuilder builder = new StringBuilder();
        buildRelativeURL(builder, this.logicalURL);
        return builder.toString();
    }

    void buildRelativeURL(StringBuilder builder, String relativeURL) {
        if (relativeURL.startsWith("/")) {
            String context = getAbsoluteContext(this.contextPath, this.deploymentContext);
            builder.append(context).append(relativeURL);
        } else {
            builder.append(relativeURL);
        }
        if (null != this.siteSettings) {
            String url = builder.toString();
            char connector = url.indexOf('?') < 0 ? '?' : '&';
            builder.append(connector).append("version=").append(this.siteSettings.getVersion());
        }
    }

    private String getAbsoluteContext(String servletContextPath, String deploymentContext) {
        if (StringUtils.hasText(deploymentContext)) {
            if ("/".equals(deploymentContext))
                return "";
            return deploymentContext;
        }
        if ("/".equals(servletContextPath))
            return "";
        return servletContextPath;
    }

    private void buildURLPrefix(StringBuilder builder, String scheme) {
        String schemaInLowerCase = scheme.toLowerCase();
        builder.append(schemaInLowerCase).append("://").append(this.serverName);

        if (("http".equals(schemaInLowerCase)) && (this.httpPort != 80))
            builder.append(":").append(this.httpPort);
        else if (("https".equals(schemaInLowerCase)) && (this.httpsPort != 443))
            builder.append(":").append(this.httpsPort);
    }

    public void setContext(String contextPath, String deploymentContext) {
        this.contextPath = contextPath;
        this.deploymentContext = deploymentContext;
    }

    public void setServerInfo(String serverName, int httpPort, int httpsPort) {
        this.serverName = serverName;
        this.httpPort = httpPort;
        this.httpsPort = httpsPort;
    }

    public void setLogicalURL(String logicalURL) {
        this.logicalURL = logicalURL;
    }

    public void setSiteSettings(SiteSettings siteSettings) {
        this.siteSettings = siteSettings;
    }
}
