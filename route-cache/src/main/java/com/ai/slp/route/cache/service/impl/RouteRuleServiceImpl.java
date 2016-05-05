package com.ai.slp.route.cache.service.impl;

import com.ai.slp.route.cache.dao.inter.IRouteRuleDao;
import com.ai.slp.route.cache.entity.RouteRule;
import com.ai.slp.route.cache.service.inter.IRouteRuleService;

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
