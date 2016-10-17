package org.matrix.framework.core.database;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.annotation.Transactional;

//实现数据库读写分离,水平扩展的核心类
public class DBSwitchInterceptor implements MethodInterceptor {

    private JDBCAccessContext jdbcAccessContext;
    private JDBCAccessFactory factory;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        Transactional transactional = (Transactional) method.getAnnotation(Transactional.class);
        Switch zwitch = (Switch) method.getAnnotation(Switch.class);
        // 只要方法上有Transaction注解,就获取主库(写库)的连接
        if (null != transactional) {
            this.jdbcAccessContext.setJdbcAccess(this.factory.getMasterJDBCAccess());
            // 方法上没有Transaction注解,且有Switch注解,则根据Switch注解的值获取主库或从库的连接
        } else if (null != zwitch) {
            Switch.SwitchType switchType = zwitch.value();
            switch (switchType.ordinal()) {
            case 1:
                this.jdbcAccessContext.setJdbcAccess(this.factory.getMasterJDBCAccess());
                break;
            case 2:
                this.jdbcAccessContext.setJdbcAccess(this.factory.getSlaveJDBCAccess());
                break;
            }
            // 既无Transaction注解,又无Switch注解,默认获取从库的连接
        } else {
            this.jdbcAccessContext.setJdbcAccess(this.factory.getSlaveJDBCAccess());
        }
        return methodInvocation.proceed();
    }

    @Inject
    public void setJdbcAccessContext(JDBCAccessContext jdbcAccessContext) {
        this.jdbcAccessContext = jdbcAccessContext;
    }

    // 注意严格限制注入的JDBCAccessFactory的名字为jdbcAccessFactory
    @Inject
    public void setFactory(JDBCAccessFactory jdbcAccessFactory) {
        this.factory = jdbcAccessFactory;
    }

}
