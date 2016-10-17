package org.matrix.framework.core.platform.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.matrix.framework.core.platform.SpringObjectFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

public class AnnotatedMethodAdvisor implements PointcutAdvisor {

    private final Class<? extends Annotation> annotationClass;
    private final Class<? extends MethodInterceptor> interceptorClass;
    private SpringObjectFactory objectFactory;
    private MethodInterceptor interceptor;

    public AnnotatedMethodAdvisor(Class<? extends Annotation> annotationClass,
            Class<? extends MethodInterceptor> interceptorClass) {
        this.annotationClass = annotationClass;
        this.interceptorClass = interceptorClass;
    }

    @PostConstruct
    public void initialize() {
        this.interceptor = ((MethodInterceptor) this.objectFactory.createBean(this.interceptorClass));
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public Pointcut getPointcut() {
        return new StaticMethodMatcherPointcut() {
            public boolean matches(Method method, Class<?> targetClass) {
                return method.isAnnotationPresent(AnnotatedMethodAdvisor.this.annotationClass);
            }
        };
    }

    @Inject
    public void setObjectFactory(SpringObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

}
