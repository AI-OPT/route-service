package com.ai.slp.route.dao;

import com.ai.slp.route.vo.Route;

import java.sql.SQLException;

/**
 * Created by xin on 16-4-29.
 */
public interface IRouteDao {
    boolean checkStatusIsValidate(String routeId) throws SQLException;

    Route queryRouteById(String routeId) throws SQLException;

    String queryServiceIdByRouteId(String routeId) throws SQLException;
}
