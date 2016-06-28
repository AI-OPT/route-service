package com.ai.slp.route.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by xin on 16-4-29.
 */
public class DBQueryTemplate {

    private static Logger logger = LogManager.getLogger(DBQueryTemplate.class);

    public static <R> R query(Executor<R> executor) throws SQLException {
        Connection connection = null;
        try {
            connection = DBUtils.getConnection();
            return executor.query(connection);
        } catch (Exception e) {
            logger.error(e);
            logger.error("Failed to execute query.", e);
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public interface Executor<R> {
        R query(Connection connection) throws SQLException;
    }
}
