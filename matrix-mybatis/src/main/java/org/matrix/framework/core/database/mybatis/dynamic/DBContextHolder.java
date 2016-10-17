package org.matrix.framework.core.database.mybatis.dynamic;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

public class DBContextHolder {

    private final DataSource[] slaveDataSources;

    private final AtomicInteger identity = new AtomicInteger();

    public DBContextHolder(DataSource[] slaveDataSources) {
        this.slaveDataSources = slaveDataSources;
    }

    private static ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<DataSourceType>();

    public String getDataSourceKey() {
        DataSourceType dataSourceType = contextHolder.get();
        if (null == dataSourceType || DataSourceType.SLAVE.equals(dataSourceType)) {
            return "slave" + mod(slaveDataSources.length);
        }
        return "master";
    }

    /**
     * 指定本线程的DataSourceType
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        contextHolder.set(dataSourceType);
    }

    /**
     * 清除数据源类型
     */
    public static void clearDataSourceType() {
        contextHolder.remove();
    }

    private int mod(int length) {
        int mod = this.identity.incrementAndGet() % length;
        if (length == 1)
            return 0;
        if (mod < length) {
            return mod;
        }
        return mod(length);
    }

}
