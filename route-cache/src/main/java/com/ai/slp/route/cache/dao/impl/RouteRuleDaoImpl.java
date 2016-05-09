package com.ai.slp.route.cache.dao.impl;

import com.ai.slp.route.cache.dao.inter.IRouteRuleDao;
import com.ai.slp.route.cache.dto.RouteRule;
import com.ai.slp.route.common.entity.RuleBaseInfo;
import com.ai.slp.route.common.util.DBQueryTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xin on 16-4-29.
 */
public class RouteRuleDaoImpl implements IRouteRuleDao {
    @Override
    public List<RouteRule> queryRouteRuleByRouteId(final String routeId) throws SQLException {
        final String sql = "SELECT ROUTE_RULE_ID, ROUTE_ID, ROUTE_RULE_TYPE, STATE,ROUTE_RULE_ITEM, MIN_QUANTITY, MAX_QUANTITY, TIME_TYPE, CYCLE_UNIT, CYCLE_VALUE, BEGIN_DATE, END_DATE FROM ROUTE_RULE WHERE ROUTE_ID = ? AND STATE LIKE ?";
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<List<RouteRule>>() {
            @Override
            public List<RouteRule> query(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, routeId);
                preparedStatement.setString(2, "2%");

                ResultSet resultSet = preparedStatement.executeQuery();
                List<RouteRule> routeRules = new ArrayList<RouteRule>();

                while (resultSet.next()) {
                    RouteRule routeRule = new RouteRule(resultSet.getString("ROUTE_ID"),
                            resultSet.getString("ROUTE_RULE_ID"), resultSet.getString("STATE"));
                    routeRule.setRuleBaseInfo(new RuleBaseInfo(resultSet));
                    routeRules.add(routeRule);
                }

                return routeRules;
            }
        });
    }

    @Override
    public RouteRule queryRouteRuleById(final String ruleId) throws SQLException {
        final String sql = "SELECT ROUTE_RULE_ID, ROUTE_ID, ROUTE_RULE_TYPE, STATE,ROUTE_RULE_ITEM, MIN_QUANTITY, MAX_QUANTITY, TIME_TYPE, CYCLE_UNIT, CYCLE_VALUE, BEGIN_DATE, END_DATE FROM ROUTE_RULE WHERE ROUTE_RULE_ID = ? AND STATE LIKE ?";
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<RouteRule>() {
            @Override
            public RouteRule query(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, ruleId);
                preparedStatement.setString(2, "2%");

                ResultSet resultSet = preparedStatement.executeQuery();

                RouteRule routeRule = null;

                if (resultSet.next()) {
                    routeRule = new RouteRule(resultSet.getString("ROUTE_ID"),
                            resultSet.getString("ROUTE_RULE_ID"), resultSet.getString("STATE"));
                    routeRule.setRuleBaseInfo(new RuleBaseInfo(resultSet));
                }

                return routeRule;
            }
        });
    }
}
