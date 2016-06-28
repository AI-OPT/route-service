package com.ai.slp.route.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Component
public class DBUtils {

    @Autowired
    private DataSource dataSourceTmp;

    private static DataSource dataSource;

    private static Logger logger = LogManager.getLogger(DBUtils.class);


    @PostConstruct
    private void initDataSource() {
        dataSource = dataSourceTmp;
    }


    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to get connection", e);
            throw new RuntimeException("Cannot get connection.");
        }

    }
}
