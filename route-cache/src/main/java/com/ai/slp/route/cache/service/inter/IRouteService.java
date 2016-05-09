package com.ai.slp.route.cache.service.inter;

import com.ai.slp.route.cache.dto.Route;

import java.sql.SQLException;

public interface IRouteService {
    Route queryRouteById(String routeId) throws SQLException;
}
