package org.matrix.framework.core.platform.web.freemarker.tag;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.platform.web.site.URLBuilder;
import org.matrix.framework.core.settings.CDNSettings;
import org.matrix.framework.core.settings.DeploymentSettings;
import org.matrix.framework.core.settings.SiteSettings;
import org.matrix.framework.core.util.AssertUtils;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;

public class CDNTagSupport extends TagSupport {
    final HttpServletRequest request;
    final SiteSettings siteSettings;
    final DeploymentSettings deploymentSettings;
    final CDNSettings cdnSettings;

    public CDNTagSupport(HttpServletRequest request, SiteSettings siteSettings, DeploymentSettings deploymentSettings, CDNSettings cdnSettings) {
        this.request = request;
        this.siteSettings = siteSettings;
        this.deploymentSettings = deploymentSettings;
        this.cdnSettings = cdnSettings;
    }

    String constructCDNPrefix(String url) {
        if (!supportCDN())
            return constructLocalURL(url, true);
        String cdnHost = determineCDNHost(url);
        StringBuilder builder = new StringBuilder();
        builder.append(request.getScheme()).append("://").append(cdnHost).append(url);
        return builder.toString();
    }

    String constructCDNURL(String url) {
        if (!supportCDN())
            return constructLocalURL(url, true);
        char connector = url.indexOf('?') < 0 ? '?' : '&';
        StringBuilder builder = new StringBuilder();
        builder.append(constructCDNPrefix(url)).append(connector).append("version=").append(siteSettings.getVersion());
        return builder.toString();
    }

    public String constructNFSURL(String url) {
        if (!supportNFSCDN())
            return constructLocalURL(url, false); // 不支持CDN
        String nfsHost = determineNFSHost(url);
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(nfsHost).append("/fstatic").append(url);
        return builder.toString();
    }

    boolean supportCDN() {
        String[] cdnHosts = cdnSettings.getCDNHosts();
        if (cdnHosts == null || cdnHosts.length == 0)
            return false;
        if (request.isSecure() && !cdnSettings.supportHTTPS())
            return false;
        return true;
    }

    public boolean supportNFSCDN() {
        String[] cdnHosts = cdnSettings.getNFSHosts();
        if (cdnHosts == null || cdnHosts.length == 0)
            return false;
        if (request.isSecure() && !cdnSettings.supportHTTPS())
            return false;
        return true;
    }

    /**
     * use hash to generate deterministic spread cdn hosts
     *
     * @param url
     *            the relative url
     * @return cdn host
     */
    String determineCDNHost(String url) {
        int index = Math.abs(url.hashCode() % cdnSettings.getCDNHosts().length);
        return cdnSettings.getCDNHosts()[index];
    }

    public String determineNFSHost(String url) {
        int index = Math.abs(url.hashCode() % cdnSettings.getNFSHosts().length);
        return cdnSettings.getNFSHosts()[index];
    }

    private String constructLocalURL(String url, boolean hasVersion) {
        URLBuilder builder = new URLBuilder();
        builder.setContext(request.getContextPath(), deploymentSettings.getDeploymentContext());
        if (hasVersion)
            builder.setSiteSettings(siteSettings);
        return builder.constructRelativeURL(url);
    }

    // TODO: tuning by not using String.format, according to profiling result
    String buildMultipleResourceTags(String srcKey, String resourceDir, String tagTemplate, Map<String, Object> params) throws IOException, TemplateModelException {
        StringBuilder builder = new StringBuilder();

        String srcValue = getRequiredStringParam(params, srcKey);

        String[] srcItems = srcValue.split(",");
        for (String srcItem : srcItems) {
            String src = srcItem.trim();
            AssertUtils.assertHasText(src, "src can not be empty");
            if (!src.startsWith("/")) {
                src = String.format("%s/%s", resourceDir, src);
            }
            src = constructCDNURL(src);
            String code = String.format(tagTemplate, src, buildExtAttributes(params, srcKey));
            builder.append(code).append("\n");
        }
        return builder.toString();
    }

    String buildExtAttributes(Map<String, Object> params, String excludedKey) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            String key = param.getKey();
            if (!excludedKey.equals(key)) {
                Object value = param.getValue();
                if (value instanceof SimpleScalar) {
                    builder.append(String.format(" %s=\"%s\"", key, value));
                }
            }
        }
        return builder.toString();
    }
}
