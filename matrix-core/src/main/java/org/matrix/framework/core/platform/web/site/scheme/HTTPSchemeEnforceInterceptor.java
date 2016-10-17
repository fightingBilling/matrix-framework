package org.matrix.framework.core.platform.web.site.scheme;

import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.matrix.framework.core.platform.web.filter.RequestUtils;
import org.matrix.framework.core.platform.web.site.URLBuilder;
import org.matrix.framework.core.settings.DeploymentSettings;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 对请求模式有特殊标注的的类和方法进行强制转换
 * 
 * @author pankai
 *
 */
public class HTTPSchemeEnforceInterceptor extends HandlerInterceptorAdapter {

    private DeploymentSettings deploymentSettings;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ((handler instanceof HandlerMethod)) {
            HTTPOnly httpOnly = (HTTPOnly) findAnnotation((HandlerMethod) handler, HTTPOnly.class);
            if ((httpOnly != null) && (!"http".equals(request.getScheme()))) {
                enforceScheme(request, response, "http");
                return false;
            }

            HTTPSOnly httpsOnly = (HTTPSOnly) findAnnotation((HandlerMethod) handler, HTTPSOnly.class);
            if ((httpsOnly != null) && (!"https".equals(request.getScheme()))) {
                enforceScheme(request, response, "https");
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> T findAnnotation(HandlerMethod handler, Class<T> annotationType) {
        Annotation annotation = handler.getBeanType().getAnnotation(annotationType);
        if (annotation != null)
            return (T) annotation;
        return handler.getMethodAnnotation(annotationType);
    }

    private void enforceScheme(HttpServletRequest request, HttpServletResponse response, String scheme) {
        URLBuilder builder = new URLBuilder();
        builder.setContext(request.getContextPath(), this.deploymentSettings.getDeploymentContext());
        builder.setServerInfo(request.getServerName(), this.deploymentSettings.getHttpPort(), this.deploymentSettings
                .getHttpsPort());
        response.setStatus(301);
        response.setHeader("Location", builder.constructAbsoluteURL(scheme, RequestUtils
                .getClientRelativeRequestURLWithQueryString(request)));
    }

    @Inject
    public void setDeploymentSettings(DeploymentSettings deploymentSettings) {
        this.deploymentSettings = deploymentSettings;
    }

}
