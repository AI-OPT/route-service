package com.ai.slp.route.service.atom.impl;

import com.ai.slp.route.cache.dao.IRouteRuleDao;
import com.ai.slp.route.cache.dto.RouteRule;
import com.ai.slp.route.service.atom.interfaces.IRouteRuleService;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-3.
 */
public class RouteRuleServiceImpl implements IRouteRuleService {

    private IRouteRuleDao routeRuleDao;

    @Override
    public RouteRule queryRouteRuleById(String ruleId) throws SQLException {
        return routeRuleDao.queryRouteRuleById(ruleId);
    }

    public void setRouteRuleDao(IRouteRuleDao routeRuleDao) {
        this.routeRuleDao = routeRuleDao;
    }
}
