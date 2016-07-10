package com.ai.slp.route.service.atom.interfaces;

import com.ai.slp.route.vo.RouteRule;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-3.
 */
public interface IRouteRuleService {
    RouteRule queryRouteRuleById(String ruleId) throws SQLException;
}
