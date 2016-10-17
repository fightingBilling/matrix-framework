package org.matrix.framework.core.database.mybatis.dynamic;

import javax.inject.Inject;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * spring每次连数据库的时候,调用这个方法获取目标key.
 * @author pankai
 * 2016年1月18日
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private DBContextHolder dbContextHolder;

    @Inject
    public void setDbContextHolder(DBContextHolder dbContextHolder) {
        this.dbContextHolder = dbContextHolder;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dbContextHolder.getDataSourceKey();
    }
}
