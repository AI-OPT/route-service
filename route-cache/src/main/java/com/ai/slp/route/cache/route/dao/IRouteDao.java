package com.ai.slp.route.cache.route.dao;

import com.ai.slp.route.cache.route.dto.Route;

import java.sql.SQLException;

/**
 * Created by xin on 16-4-29.
 */
public interface IRouteDao {
    boolean checkStatusIsValidate(String routeId) throws SQLException;

    Route queryRouteById(String routeId) throws SQLException;

    String queryServiceIdByRouteId(String routeId) throws SQLException;
}
