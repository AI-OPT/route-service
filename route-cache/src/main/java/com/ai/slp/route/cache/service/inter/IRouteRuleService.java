package com.ai.slp.route.cache.service.inter;

import com.ai.slp.route.cache.entity.RouteRule;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-3.
 */
public interface IRouteRuleService {
    RouteRule queryRouteRuleById(String ruleId) throws SQLException;
}
