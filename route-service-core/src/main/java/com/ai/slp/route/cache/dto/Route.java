package com.ai.slp.route.cache.dto;

import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xin on 16-4-29.
 */
public class Route {
    private Logger logger = LoggerFactory.getLogger(Route.class);
    private String routeId;
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

    /**
     * 刷新路由下规则数据
     */
    public void refreshRouteRuleData() {
        for (RouteRule routeRule : routeRules) {
            routeRule.refreshCache();
        }
    }

    /**
     * 刷新路由数据
     */
    public void refreshRouteData() {
        Map<String, String> ruleBaseInfoMap = new HashMap<String, String>();
        for (RouteRule routeRule : routeRules) {
            ruleBaseInfoMap.put(routeRule.getRuleId(), new Gson().toJson(routeRule.getRuleBaseInfo()));
        }
        String routeDataKey = CacheKeyUtil.RK_Route(routeId);
        MCSUtil.expire(routeDataKey);
        MCSUtil.hput(routeDataKey, ruleBaseInfoMap);
        logger.debug("Refresh key:{}, Refsh Value:{}", routeDataKey, ruleBaseInfoMap);

        String routeStatusKey = CacheKeyUtil.RK_RouteStatus(routeId);
        MCSUtil.expire(routeStatusKey);
        MCSUtil.put(routeStatusKey, routeStatus.getValue());
        logger.debug("Refresh key:{}, Refsh Value:{}", routeStatusKey, routeStatus.getValue());
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
