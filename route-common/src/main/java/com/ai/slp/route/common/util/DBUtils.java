package com.ai.slp.route.common.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by xin on 16-4-25.
 */
public class DBUtils {
    private static Logger logger = LogManager.getLogger(DBUtils.class);

    private static HikariDataSource hikariDataSource;
    private static Properties dbConfig;

    static {
        dbConfig = new Properties();
        InputStream inputStream = DBUtils.class.getResourceAsStream("/jdbc.properties");
        try {
            dbConfig.load(inputStream);
        } catch (IOException e) {
            logger.error("Failed to load jdbc.properties.");
            logger.error(e);
            System.exit(-1);
        }
    }

    public static Connection getConnection() {
        if (hikariDataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbConfig.getProperty("db.url"));
            config.setUsername(dbConfig.getProperty("db.user_name"));
            config.setPassword(dbConfig.getProperty("db.password"));
            config.setDriverClassName(dbConfig.getProperty("db.driver_class"));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.setMinimumIdle(Integer.parseInt(dbConfig.getProperty("db.max_idle", "1")));
            config.setMaximumPoolSize(Integer.parseInt(dbConfig.getProperty("db.max_pool_size", "20")));
            config.setConnectionTimeout(Integer.parseInt(dbConfig.getProperty("db.connect_timeout","10000")));
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariDataSource = new HikariDataSource(config);
        }
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to get connection", e);
            throw new RuntimeException("Cannot get connection.");
        }

    }
}
