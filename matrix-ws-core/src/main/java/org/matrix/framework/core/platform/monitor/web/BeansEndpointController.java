package org.matrix.framework.core.platform.monitor.web;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.platform.monitor.Pass;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 使用json展示应用内使用的bean
 * @author pankai
 * Nov 4, 2015
 */
@Controller
public class BeansEndpointController extends MatrixRestController {

    private ApplicationContext applicationContext;

    @RequestMapping(value = "/monitor/beans", produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.GET)
    @ResponseBody
    @Pass
    public String beanView(HttpServletRequest request) {
        Set<ConfigurableApplicationContext> set = new HashSet<ConfigurableApplicationContext>();
        set.add((ConfigurableApplicationContext) applicationContext);
        return generateJson(set);
    }

    /**
     * Actually generate a JSON snapshot of the beans in the given ApplicationContexts.
     * <p>This implementation doesn't use any JSON parsing libraries in order to avoid
     * third-party library dependencies. It produces an array of context description
     * objects, each containing a context and parent attribute as well as a beans
     * attribute with nested bean description objects. Each bean object contains a
     * bean, scope, type and resource attribute, as well as a dependencies attribute
     * with a nested array of bean names that the present bean depends on.
     * @param contexts the set of ApplicationContexts
     * @return the JSON document
     */
    private String generateJson(Set<ConfigurableApplicationContext> contexts) {
        StringBuilder result = new StringBuilder("[\n");
        for (Iterator<ConfigurableApplicationContext> it = contexts.iterator(); it.hasNext();) {
            ConfigurableApplicationContext context = it.next();
            result.append("{\n\"context\": \"").append(context.getId()).append("\",\n");
            if (context.getParent() != null) {
                result.append("\"parent\": \"").append(context.getParent().getId()).append("\",\n");
            } else {
                result.append("\"parent\": null,\n");
            }
            result.append("\"beans\": [\n");
            ConfigurableListableBeanFactory bf = context.getBeanFactory();
            String[] beanNames = bf.getBeanDefinitionNames();
            boolean elementAppended = false;
            for (String beanName : beanNames) {
                BeanDefinition bd = bf.getBeanDefinition(beanName);
                if (isBeanEligible(beanName, bd, bf)) {
                    if (elementAppended) {
                        result.append(",\n");
                    }
                    result.append("{\n\"bean\": \"").append(beanName).append("\",\n");
                    String scope = bd.getScope();
                    if (!StringUtils.hasText(scope)) {
                        scope = BeanDefinition.SCOPE_SINGLETON;
                    }
                    result.append("\"scope\": \"").append(scope).append("\",\n");
                    Class<?> beanType = bf.getType(beanName);
                    if (beanType != null) {
                        result.append("\"type\": \"").append(beanType.getName()).append("\",\n");
                    } else {
                        result.append("\"type\": null,\n");
                    }
                    result.append("\"resource\": \"").append(getEscapedResourceDescription(bd)).append("\",\n");
                    result.append("\"dependencies\": [");
                    String[] dependencies = bf.getDependenciesForBean(beanName);
                    if (dependencies.length > 0) {
                        result.append("\"");
                    }
                    result.append(StringUtils.arrayToDelimitedString(dependencies, "\", \""));
                    if (dependencies.length > 0) {
                        result.append("\"");
                    }
                    result.append("]\n}");
                    elementAppended = true;
                }
            }
            result.append("]\n");
            result.append("}");
            if (it.hasNext()) {
                result.append(",\n");
            }
        }
        result.append("]");
        return result.toString();
    }

    /**
     * 如果bean的resourceDescription含有需要转义的字符,需要进行转义.
     * Determine a resource description for the given bean definition and
     * apply basic JSON escaping (backslashes, double quotes) to it.
     * @param bd the bean definition to build the resource description for
     * @return the JSON-escaped resource description
     */
    private String getEscapedResourceDescription(BeanDefinition bd) {
        String resourceDescription = bd.getResourceDescription();
        if (resourceDescription == null) {
            return null;
        }
        StringBuilder result = new StringBuilder(resourceDescription.length() + 16);
        for (int i = 0; i < resourceDescription.length(); i++) {
            char character = resourceDescription.charAt(i);
            if (character == '\\') {
                result.append('/');
            } else if (character == '"') {
                result.append("\\").append('"');
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    /**
     * 判断指定的bean是否需要显示出来.
     * Determine whether the specified bean is eligible for inclusion in the
     * LiveBeansView JSON snapshot.
     * @param beanName the name of the bean
     * @param bd the corresponding bean definition
     * @param bf the containing bean factory
     * @return {@code true} if the bean is to be included; {@code false} otherwise
     */
    private boolean isBeanEligible(String beanName, BeanDefinition bd, ConfigurableBeanFactory bf) {
        //不是框架内部使用的bean&&(不是懒加载的bean[说明已经在容器初始化的时候已经创建了bean的实例]||容器中已有一个单例bean的实例)
        return (bd.getRole() != BeanDefinition.ROLE_INFRASTRUCTURE && (!bd.isLazyInit() || bf.containsSingleton(beanName)));
    }

    @Inject
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
