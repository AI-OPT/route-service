package com.ai.slp.route.service.atom.interfaces;

import com.ai.slp.route.vo.Route;

import java.sql.SQLException;

public interface IRouteService {
    Route queryRouteById(String routeId) throws SQLException;
}
