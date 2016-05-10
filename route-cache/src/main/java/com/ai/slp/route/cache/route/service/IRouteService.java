package com.ai.slp.route.cache.route.service;

import com.ai.slp.route.cache.route.dto.Route;

import java.sql.SQLException;

public interface IRouteService {
    Route queryRouteById(String routeId) throws SQLException;
}
