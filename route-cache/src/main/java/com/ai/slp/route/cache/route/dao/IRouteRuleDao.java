package com.ai.slp.route.cache.route.dao;

import com.ai.slp.route.cache.route.dto.RouteRule;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xin on 16-4-29.
 */
public interface IRouteRuleDao {
    List<RouteRule> queryRouteRuleByRouteId(String routeId) throws SQLException;

    RouteRule queryRouteRuleById(String ruleId) throws SQLException;
}
