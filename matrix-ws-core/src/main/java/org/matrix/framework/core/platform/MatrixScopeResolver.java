package org.matrix.framework.core.platform;

import java.util.Map;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;

public class MatrixScopeResolver implements ScopeMetadataResolver {

    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        ScopeMetadata metadata = new ScopeMetadata();
        //changed from prototype to singleton on 2015.11.4.
        //改回了spring创建bean的默认行为.小心线程安全问题.
        metadata.setScopeName(BeanDefinition.SCOPE_SINGLETON);
        if (definition instanceof AnnotatedBeanDefinition) {
            resolveScopeAnnotation((AnnotatedBeanDefinition) definition, metadata);
        }
        return metadata;
    }

    private void resolveScopeAnnotation(AnnotatedBeanDefinition definition, ScopeMetadata metadata) {
        @SuppressWarnings("rawtypes")
        Map attributes = definition.getMetadata().getAllAnnotationAttributes(Scope.class.getName());
        if (null != attributes) {
            metadata.setScopeName((String) attributes.get("value"));
            ScopedProxyMode proxyMode = (ScopedProxyMode) attributes.get("proxyMode");
            if (proxyMode == null || proxyMode == ScopedProxyMode.DEFAULT) {
                proxyMode = ScopedProxyMode.NO;
            }
            metadata.setScopedProxyMode(proxyMode);
        }
    }

}
