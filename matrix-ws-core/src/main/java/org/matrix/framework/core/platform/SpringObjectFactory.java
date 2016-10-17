package org.matrix.framework.core.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.matrix.framework.core.util.AssertUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;

/**
 * 单例保持ApplicationContext的引用达到在spring容器外管理bean.
 * 
 * @author pankai 2015年6月26日
 */
public final class SpringObjectFactory {

    private SpringObjectFactory() {

    }

    private static final SpringObjectFactory SPRINGOBJECTFACTORY = new SpringObjectFactory();

    private ApplicationContext applicationContext;

    public static SpringObjectFactory getInstance() {
        return SPRINGOBJECTFACTORY;
    }

    /**
     * 创建一个指定类型的新实例.
     */
    public <T> T createBean(Class<T> beanClass) {
        return this.applicationContext.getAutowireCapableBeanFactory().createBean(beanClass);
    }

    /**
     * 返回指定类型的实例集合.
     */
    public <T> List<T> getBeans(Class<T> beanClass) {
        return new ArrayList<T>(this.applicationContext.getBeansOfType(beanClass).values());
    }

    /**
     * 返回指定类型的实际集合.
     */
    public <T> Map<String, T> getBeansOfType(Class<T> beanClass) {
        return this.applicationContext.getBeansOfType(beanClass);
    }

    /**
     * 返回一个容器内指定类型的实例.
     */
    public <T> T getBean(Class<T> beanClass) {
        return this.applicationContext.getBean(beanClass);
    }

    /**
     * 返回一个容器内指定类型的实例.带参数
     */
    public <T> T getBean(Class<T> beanClass, Object... args) {
        return this.applicationContext.getBean(beanClass, args);
    }

    /**
     * 返回一个 "指定名称" && "指定类型" 的实例.
     */
    public <T> T getBean(String beanName, Class<T> beanClass) {
        return applicationContext.getBean(beanName, beanClass);
    }

    /**
     * 初始化一个bean
     */
    @SuppressWarnings("unchecked")
    public <T> T initializeBean(T bean) {
        AutowireCapableBeanFactory factory = this.applicationContext.getAutowireCapableBeanFactory();
        factory.autowireBean(bean);
        return (T) factory.initializeBean(bean, bean.getClass().getName());
    }

    /**
     * 在容器中注册一个单例的bean
     */
    @SuppressWarnings("rawtypes")
    public void registerSingletonBean(String beanName, Class beanClass) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(beanClass);
        definition.setScope(BeanDefinition.SCOPE_SINGLETON);
        ((BeanDefinitionRegistry) this.applicationContext.getAutowireCapableBeanFactory()).registerBeanDefinition(beanName, definition);
    }

    @Inject
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        AssertUtils.assertTrue(applicationContext.getAutowireCapableBeanFactory() instanceof BeanDefinitionRegistry, "autowireCapableBeanFactory should be BeanDefinitionRegistry");
    }

}
