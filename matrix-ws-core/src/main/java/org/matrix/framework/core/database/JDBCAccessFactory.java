package org.matrix.framework.core.database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.matrix.framework.core.database.manager.SqlManager;

public class JDBCAccessFactory {

    private JDBCAccess masterAccess;
    private final List<JDBCAccess> slaveAccesses = new ArrayList<JDBCAccess>();

    private final AtomicInteger identity = new AtomicInteger();
    private SqlManager sqlManager;
    private final DataSource masterDataSource;
    private final DataSource[] slaveDataSources;
    private boolean initalized = false;

    public JDBCAccessFactory(DataSource masterDataSource, DataSource... slaveDataSources) {
        this.masterDataSource = masterDataSource;
        this.slaveDataSources = slaveDataSources;
    }

    @PostConstruct
    public void initalize() {
        if (this.initalized) {
            return;
        }
        this.slaveAccesses.clear();
        this.masterAccess = new JDBCAccess();
        this.masterAccess.setDataSource(masterDataSource);
        this.masterAccess.setSwitchName(Switch.SwitchType.Master.toString());
        this.masterAccess.setSqlManager(this.sqlManager);
        if ((null != this.slaveDataSources) && (this.slaveDataSources.length > 0)) {
            for (int i = 0; i < this.slaveDataSources.length; i++) {
                JDBCAccess slaveJDBCAccess = new JDBCAccess();
                slaveJDBCAccess.setDataSource(this.slaveDataSources[i]);
                slaveJDBCAccess.setSwitchName(Switch.SwitchType.Slave.toString() + "[" + i + "]");
                slaveJDBCAccess.setSqlManager(this.sqlManager);
                this.slaveAccesses.add(slaveJDBCAccess);
            }
        }
        this.initalized = true;
    }

    public JDBCAccess getMasterJDBCAccess() {
        return this.masterAccess;
    }

    // 根据Round-Robin算法取得从库的连接
    public JDBCAccess getSlaveJDBCAccess() {
        return (JDBCAccess) this.slaveAccesses.get(mod(this.slaveAccesses.size()));
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

    @Inject
    public void setSqlManager(SqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

}
