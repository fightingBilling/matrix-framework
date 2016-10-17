package org.matrix.framework.core.platform.monitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * matrix内部监控限制
 * @author pankai
 * Nov 5, 2015
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Order(Ordered.LOWEST_PRECEDENCE)
public @interface Pass {

}
