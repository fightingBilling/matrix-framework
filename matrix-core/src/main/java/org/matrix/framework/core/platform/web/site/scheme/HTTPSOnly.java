package org.matrix.framework.core.platform.web.site.scheme;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在类或方法上指明该类或方法只能执行在https安全连接下.
 * 
 * @author pankai
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HTTPSOnly {

}
