package com.ai.slp.route.cache.service.inter;

import com.ai.slp.route.cache.entity.Route;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-3.
 */
public interface IRouteServerService {
    Route queryRouteById(String routeId) throws SQLException;
}
