package com.ai.slp.route.cache.dao.inter;

import com.ai.slp.route.cache.entity.Route;

import java.sql.SQLException;

/**
 * Created by xin on 16-4-29.
 */
public interface IRouteDao {
    boolean checkStatusIsValidate(String routeId) throws SQLException;

    Route queryRouteById(String routeId) throws SQLException;
}
