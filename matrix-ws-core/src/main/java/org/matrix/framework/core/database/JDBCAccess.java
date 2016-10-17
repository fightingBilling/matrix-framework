package org.matrix.framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.matrix.framework.core.database.manager.SqlManager;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.StopWatch;
import org.matrix.framework.core.util.TimeLength;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class JDBCAccess {

    private final Logger logger = LoggerFactory.getLogger(JDBCAccess.class);
    private static final long SLOW_SQL_TIME_LENGTH = TimeLength.seconds(1L).toMilliseconds();
    private JdbcTemplate jdbcTemplate;
    private String switchName;
    private SqlManager sqlManager;

    /**
     * 水平分区查询
     */
    public <T> List<T> find(String aliasName, boolean multiPartition, Object[] partitions, RowMapper<T> rowMapper, Object... params) {
        StopWatch watch = new StopWatch();
        String sql = this.sqlManager.getSqlText(aliasName, multiPartition, partitions);
        try {
            return this.jdbcTemplate.query(sql, params, rowMapper);
        } finally {
            this.logger.debug("find, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("aliasName={}|sql={}|elapsedTime={}", new Object[] { aliasName, sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public <T> List<T> find(String aliasName, RowMapper<T> rowMapper, Object... params) {
        return find(aliasName, true, null, rowMapper, params);
    }

    /**
     * 无参数的列表查询.
     */
    public <T> List<T> find(String aliasName, RowMapper<T> rowMapper) {
        StopWatch watch = new StopWatch();
        String sql = this.sqlManager.getSqlText(aliasName, false, null);
        try {
            return this.jdbcTemplate.query(sql, rowMapper);
        } finally {
            this.logger.debug("find, sql={}, elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("aliasName={}|sql={}|elapsedTime={}", new Object[] { aliasName, sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public <T> List<T> findWithoutSqlManager(String sql, RowMapper<T> rowMapper, Object... params) {
        StopWatch watch = new StopWatch();
        try {
            if (params.length != 0) {
                return this.jdbcTemplate.query(sql, params, rowMapper);
            } else {
                return this.jdbcTemplate.query(sql, rowMapper);
            }
        } finally {
            this.logger.debug("find, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("sql={}|elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public <T> List<T> findWithoutSqlManager(String sql, RowMapper<T> rowMapper) {
        StopWatch watch = new StopWatch();
        try {
            return this.jdbcTemplate.query(sql, rowMapper);
        } finally {
            this.logger.debug("find, sql={}, elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("sql={}|elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public <T> T findUniqueResult(String aliasName, boolean multiPartition, Object[] partitions, RowMapper<T> rowMapper, Object... params) {
        StopWatch watch = new StopWatch();
        String sql = this.sqlManager.getSqlText(aliasName, multiPartition, partitions);
        try {
            return this.jdbcTemplate.queryForObject(sql, params, rowMapper);
        } finally {
            this.logger.debug("findUniqueResult, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("aliasName={}|sql={}|elapsedTime={}", new Object[] { aliasName, sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public <T> T findUniqueResult(String aliasName, RowMapper<T> rowMapper, Object... params) {
        return findUniqueResult(aliasName, true, null, rowMapper, params);
    }

    public <T> T findUniqueResultWithoutSqlManager(String sql, RowMapper<T> rowMapper, Object... params) {
        StopWatch watch = new StopWatch();
        try {
            return this.jdbcTemplate.queryForObject(sql, params, rowMapper);
        } finally {
            this.logger.debug("findUniqueResult, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("sql={}|elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public int findInteger(String aliasName, boolean multiPartition, Object[] partitions, Object... params) {
        StopWatch watch = new StopWatch();
        String sql = this.sqlManager.getSqlText(aliasName, multiPartition, partitions);
        try {
            return this.jdbcTemplate.queryForObject(sql, params, Integer.class);
        } finally {
            this.logger.debug("findInteger, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("aliasName={}|sql={}|elapsedTime={}", new Object[] { aliasName, sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public int findInteger(String aliasName, Object... params) {
        return findInteger(aliasName, true, null, params);
    }

    public int findInteger(String aliasName) {
        StopWatch watch = new StopWatch();
        String sql = this.sqlManager.getSqlText(aliasName);
        try {
            return this.jdbcTemplate.queryForObject(sql, Integer.class);
        } finally {
            this.logger.debug("findInteger, sql={}, elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("aliasName={}|sql={}|elapsedTime={}", new Object[] { aliasName, sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public int findIntegerWithoutSqlManager(String sql, Object... params) {
        StopWatch watch = new StopWatch();
        try {
            if (params.length != 0) {
                return this.jdbcTemplate.queryForObject(sql, Integer.class, params);
            } else {
                return this.jdbcTemplate.queryForObject(sql, Integer.class);
            }
        } finally {
            this.logger.debug("findInteger, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("sql={}|elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public String findString(String aliasName, boolean multiPartition, Object[] partitions, Object... params) {
        StopWatch watch = new StopWatch();
        String sql = this.sqlManager.getSqlText(aliasName, multiPartition, partitions);
        try {
            return (String) this.jdbcTemplate.queryForObject(sql, params, new RowMapper<String>() {
                public String mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                    return resultSet.getString(1);
                }
            });
        } finally {
            this.logger.debug("findString, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("aliasName={}|sql={}|elapsedTime={}", new Object[] { aliasName, sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public String findString(String aliasName, Object... params) {
        return findString(aliasName, true, null, params);
    }

    public String findStringWithoutSqlManager(String sql, Object... params) {
        StopWatch watch = new StopWatch();
        try {
            return (String) this.jdbcTemplate.queryForObject(sql, String.class, params);
        } finally {
            this.logger.debug("findString, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("sql={}|elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
        }

    }

    public int execute(String aliasName, boolean multiPartition, Object[] partitions, Object... params) {
        StopWatch watch = new StopWatch();
        String sql = this.sqlManager.getSqlText(aliasName, multiPartition, partitions);
        try {
            return this.jdbcTemplate.update(sql, params);
        } finally {
            this.logger.debug("execute, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("aliasName={}|sql={}|elapsedTime={}", new Object[] { aliasName, sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public int execute(String aliasName, Object... params) {
        return execute(aliasName, true, null, params);
    }

    public int executeWithoutSqlManager(String sql, Object... params) {
        StopWatch watch = new StopWatch();
        try {
            return this.jdbcTemplate.update(sql, params);
        } finally {
            this.logger.debug("execute, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("sql={}|elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public int[] batchExecute(String aliasName, boolean multiPartition, Object[] partitions, List<Object[]> params) {
        StopWatch watch = new StopWatch();
        String sql = this.sqlManager.getSqlText(aliasName, multiPartition, partitions);
        try {
            return this.jdbcTemplate.batchUpdate(sql, params);
        } finally {
            this.logger.debug("batchExecute, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("aliasName={}|sql={}|elapsedTime={}", new Object[] { aliasName, sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public int[] batchExecute(String aliasName, List<Object[]> params) {
        return batchExecute(aliasName, true, null, params);
    }

    public int[] batchExecuteWithoutSqlManager(String sql, List<Object[]> params) {
        StopWatch watch = new StopWatch();
        try {
            return this.jdbcTemplate.batchUpdate(sql, params);
        } finally {
            this.logger.debug("batchExecute, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("sql={}|elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    /**
     * 插入数据并且获得自增的id
     */
    public int insertAndGetIdWithoutSqlManager(String sql, Object... params) {
        StopWatch watch = new StopWatch();
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    int i = 1;
                    for (Object param : params) {
                        stmt.setObject(i, param);
                        i++;
                    }
                    return stmt;
                }
            }, keyHolder);
            return keyHolder.getKey().intValue();
        } finally {
            this.logger.debug("insertAndGetId, sql={}, params={}, elapsedTime={}", new Object[] { sql, params, Long.valueOf(watch.elapsedTime()) });
            if (watch.elapsedTime() > SLOW_SQL_TIME_LENGTH)
                LoggerFactory.SQLLOGGER.getLogger().warn("sql={}|elapsedTime={}", new Object[] { sql, Long.valueOf(watch.elapsedTime()) });
        }
    }

    public int insertAndGetId(String aliasName, Object... params) {
        String sql = this.sqlManager.getSqlText(aliasName, false, null);
        return insertAndGetIdWithoutSqlManager(sql, params);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource() {
        return this.jdbcTemplate.getDataSource();
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getSwitchName() {
        return this.switchName;
    }

    public void setSqlManager(SqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

}
