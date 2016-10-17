package org.matrix.framework.core.platform.web.site;

import java.lang.annotation.Annotation;

import org.matrix.framework.core.platform.web.SpringController;

import org.springframework.web.method.HandlerMethod;

public class SiteHelper {

    public static boolean isSiteController(Object handler) {
        return handler instanceof HandlerMethod && (((HandlerMethod) handler).getBean() instanceof SpringController);
    }

    public static boolean isVendorController(Object handler, Class<? extends Annotation> annotationClass) {
        return (isSiteController(handler)) && (((HandlerMethod) handler).getBean().getClass().isAnnotationPresent(annotationClass));
    }
}
