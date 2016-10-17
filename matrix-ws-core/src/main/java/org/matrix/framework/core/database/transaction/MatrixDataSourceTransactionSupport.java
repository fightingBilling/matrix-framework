package org.matrix.framework.core.database.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class MatrixDataSourceTransactionSupport {

    public static Connection getConnection(DataSource dataSouece) throws SQLException {
        TransactionSynchronizationManager.initSynchronization();
        Connection conn = DataSourceUtils.getConnection(dataSouece);
        conn.setAutoCommit(false);
        return conn;
    }

    public static void rollback(Connection conn) throws SQLException {
        conn.rollback();
    }

    public static void commit(Connection conn) throws SQLException {
        conn.commit();
    }

}
