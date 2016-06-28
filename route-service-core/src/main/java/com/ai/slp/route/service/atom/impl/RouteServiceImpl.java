package com.ai.slp.route.service.atom.impl;

import com.ai.slp.route.cache.dao.IRouteDao;
import com.ai.slp.route.cache.dao.IRouteRuleDao;
import com.ai.slp.route.cache.dto.Route;
import com.ai.slp.route.service.atom.interfaces.IRouteService;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-3.
 */
public class RouteServiceImpl implements IRouteService {

    private IRouteDao routeDao;

    private IRouteRuleDao routeRuleDao;

    @Override
    public Route queryRouteById(String routeId) throws SQLException {
        Route route = routeDao.queryRouteById(routeId);
        if (route != null) {
            route.setRouteRules(routeRuleDao.queryRouteRuleByRouteId(routeId));
        }
        return route;
    }

    public void setRouteRuleDao(IRouteRuleDao routeRuleDao) {
        this.routeRuleDao = routeRuleDao;
    }

    public void setRouteDao(IRouteDao routeDao) {
        this.routeDao = routeDao;
    }
}
