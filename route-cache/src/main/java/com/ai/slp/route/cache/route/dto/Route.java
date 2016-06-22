package com.ai.slp.route.cache.route.dto;

import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.util.MCSUtil;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xin on 16-4-29.
 */
public class Route {
    private Logger logger = LogManager.getLogger(Route.class);
    private String routeId;
    private String routeName;
    private RouteInfo routeInfo;
    private List<RouteRule> routeRules;
    private RouteStatus routeStatus;

    public Route(String routeId, String state) {
        this.routeId = routeId;
        routeStatus = RouteStatus.convert(state);
        routeRules = new ArrayList<RouteRule>();
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteRules(List<RouteRule> routeRules) {
        this.routeRules.addAll(routeRules);
    }

    public void refreshCache() {
        refreshRouteRuleData();
        refreshRouteData();
    }

    public void refreshRouteRuleData() {
        for (RouteRule routeRule : routeRules) {
            routeRule.refreshCache();
        }
    }

    public void refreshRouteData() {
        Map<String, String> ruleBaseInfoMap = new HashMap<String, String>();
        for (RouteRule routeRule : routeRules) {
            ruleBaseInfoMap.put(routeRule.getRuleId(), new Gson().toJson(routeRule.getRuleBaseInfo()));
        }

        MCSUtil.expire(RedisKeyConfig.RK_Route(routeId));
        MCSUtil.hput(RedisKeyConfig.RK_Route(routeId), ruleBaseInfoMap);
        logger.debug("Refresh key:{}, Refsh Value:{}", RedisKeyConfig.RK_Route(routeId), ruleBaseInfoMap);
        MCSUtil.expire(RedisKeyConfig.RK_RouteStatus(routeId));
        MCSUtil.put(RedisKeyConfig.RK_RouteStatus(routeId), routeStatus.getValue());
        logger.debug("Refresh key:{}, Refsh Value:{}", RedisKeyConfig.RK_RouteStatus(routeId), routeStatus.getValue());
    }

    public enum RouteStatus {
        VALIDATE("N"), INVALIDATE("U");

        private String value;

        RouteStatus(String value) {
            this.value = value;
        }

        public static RouteStatus convert(String state) {
            switch (state) {
                case "2": {
                    return VALIDATE;
                }
                case "21": {
                    return INVALIDATE;
                }
                default: {
                    throw new RuntimeException("Cannot find the state[" + state + "]");
                }
            }
        }

        public String getValue() {
            return value;
        }
    }

    public List<RouteRule> getRouteRules() {
        return routeRules;
    }

    public RouteStatus getRouteStatus() {
        return routeStatus;
    }
}
