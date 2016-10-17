package org.matrix.framework.core.database;

import java.util.List;

public class JDBCAccessContext {

    private JDBCAccess jdbcAccess;

    public <T> List<T> find(String aliasName, boolean multiPartition, Object[] partitions, RowMapper<T> rowMapper, Object... params) {
        return this.jdbcAccess.find(aliasName, multiPartition, partitions, rowMapper, params);
    }

    public <T> List<T> find(String aliasName, RowMapper<T> rowMapper, Object... params) {
        return this.jdbcAccess.find(aliasName, rowMapper, params);
    }

    public <T> List<T> findWithoutSqlManager(String sql, RowMapper<T> rowMapper, Object... params) {
        return this.jdbcAccess.findWithoutSqlManager(sql, rowMapper, params);
    }

    public <T> List<T> findWithoutSqlManager(String sql, RowMapper<T> rowMapper) {
        return this.jdbcAccess.findWithoutSqlManager(sql, rowMapper);
    }

    public <T> T findUniqueResult(String aliasName, boolean multiPartition, Object[] partitions, RowMapper<T> rowMapper, Object... params) {
        return this.jdbcAccess.findUniqueResult(aliasName, multiPartition, partitions, rowMapper, params);
    }

    public <T> T findUniqueResult(String aliasName, RowMapper<T> rowMapper, Object... params) {
        return this.jdbcAccess.findUniqueResult(aliasName, rowMapper, params);
    }

    public <T> T findUniqueResultWithoutSqlManager(String sql, RowMapper<T> rowMapper, Object... params) {
        return this.jdbcAccess.findUniqueResultWithoutSqlManager(sql, rowMapper, params);
    }

    public int findInteger(String aliasName, boolean multiPartition, Object[] partitions, Object... params) {
        return this.jdbcAccess.findInteger(aliasName, multiPartition, partitions, params);
    }

    public int findInteger(String aliasName, Object... params) {
        return this.jdbcAccess.findInteger(aliasName, params);
    }

    public int findInteger(String aliasName) {
        return this.jdbcAccess.findInteger(aliasName);
    }

    public int findIntegerWithoutSqlManager(String sql, Object... params) {
        return this.jdbcAccess.findIntegerWithoutSqlManager(sql, params);
    }

    public String findString(String aliasName, boolean multiPartition, Object[] partitions, Object... params) {
        return this.jdbcAccess.findString(aliasName, multiPartition, partitions, params);
    }

    public String findString(String aliasName, Object[] params) {
        return this.jdbcAccess.findString(aliasName, params);
    }

    public String findStringWithoutSqlManager(String sql, Object... params) {
        return this.jdbcAccess.findStringWithoutSqlManager(sql, params);
    }

    public int execute(String aliasName, boolean multiPartition, Object[] partitions, Object... params) {
        return this.jdbcAccess.execute(aliasName, multiPartition, partitions, params);
    }

    public int execute(String aliasName, Object... params) {
        return this.jdbcAccess.execute(aliasName, params);
    }

    public int executeWithoutSqlManager(String sql, Object... params) {
        return this.jdbcAccess.executeWithoutSqlManager(sql, params);
    }

    public int[] batchExecute(String aliasName, boolean multiPartition, Object[] partitions, List<Object[]> params) {
        return this.jdbcAccess.batchExecute(aliasName, multiPartition, partitions, params);
    }

    public int[] batchExecute(String aliasName, List<Object[]> params) {
        return this.jdbcAccess.batchExecute(aliasName, params);
    }

    public int[] batchExecuteWithoutSqlManager(String sql, List<Object[]> params) {
        return this.jdbcAccess.batchExecuteWithoutSqlManager(sql, params);
    }

    public int insertAndGetIdWithoutSqlManager(String sql, Object... params) {
        return this.jdbcAccess.insertAndGetIdWithoutSqlManager(sql, params);
    }

    public int insertAndGetId(String aliasName, Object... params) {
        return this.jdbcAccess.insertAndGetId(aliasName, params);
    }

    public JDBCAccess getJdbcAccess() {
        return jdbcAccess;
    }

    public void setJdbcAccess(JDBCAccess jdbcAccess) {
        this.jdbcAccess = jdbcAccess;
    }

}
