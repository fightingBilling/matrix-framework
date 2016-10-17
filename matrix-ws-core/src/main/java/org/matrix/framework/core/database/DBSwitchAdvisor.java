package org.matrix.framework.core.database;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.matrix.framework.core.platform.intercept.AnnotatedMethodAdvisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

public class DBSwitchAdvisor extends AnnotatedMethodAdvisor {

    public DBSwitchAdvisor(Class<? extends Annotation> annotationClass,
            Class<? extends MethodInterceptor> interceptorClass) {
        super(Switch.class, interceptorClass);
    }

    public Pointcut getPointcut() {
        return new StaticMethodMatcherPointcut() {
            public boolean matches(Method method, Class<?> targetClass) {
                return DBSwitchAdvisor.this.isAnnotationPresent(targetClass);
            }
        };
    }

    private boolean isAnnotationPresent(Class<?> targetClass) {
        Class<?> superClass = targetClass.getSuperclass();
        while ((null != superClass) && (!superClass.equals(Object.class))) {
            if (superClass.equals(DBSwitch.class))
                return true;
            superClass = superClass.getSuperclass();
        }
        return false;
    }

}
