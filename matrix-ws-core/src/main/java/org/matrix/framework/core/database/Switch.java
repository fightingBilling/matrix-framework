package org.matrix.framework.core.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD })
public @interface Switch {
    public abstract SwitchType value();

    public static enum SwitchType {
        Master, Slave;
    }
}
