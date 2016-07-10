package com.ai.slp.route.service.atom.impl;

import com.ai.slp.route.dao.IRouteRuleDao;
import com.ai.slp.route.service.atom.interfaces.IRouteRuleService;
import com.ai.slp.route.vo.RouteRule;

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
