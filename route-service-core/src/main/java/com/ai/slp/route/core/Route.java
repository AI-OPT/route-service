package com.ai.slp.route.core;

import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由信息
 * Created by xin on 16-4-27.
 */
public class Route {
    private Logger logger = LogManager.getLogger(Route.class);
    private List<RouteRule> routeRules;
    private String routeId;

    public String getRouteId() {
        return routeId;
    }

    public Route(String routeId, Map<String, String> routeRulesMapping) {
        this.routeId = routeId;
        routeRules = new ArrayList<RouteRule>();
        for (Map.Entry<String, String> entry : routeRulesMapping.entrySet()) {
            routeRules.add(new RouteRule(entry.getKey(), entry.getValue()));
        }
    }

    public static Route load(String routeId) {
        String routeStatus = MCSUtil.load(CacheKeyUtil.RK_RouteStatus(routeId));
        // 先判断状态
        if (!"N".equals(routeStatus)) {
            return null;
        }
        //
        Map<String, String> routeRulesMapping = MCSUtil.hLoads(CacheKeyUtil.RK_Route(routeId));
        return new Route(routeId, routeRulesMapping);
    }

    public boolean isOutOfRules(String dataJson) {
        Map<String, Float> hasBeenIncrement = new HashMap<String, Float>();
        Map<String, Float> dataMap = new Gson().fromJson(dataJson,
                new TypeToken<Map<String, Float>>() {
                }.getType());

        boolean result = false;

        for (RouteRule rule : routeRules) {
            String routeRuleStatus = MCSUtil.load(CacheKeyUtil.RK_RouteRuleStatus(rule.getRuleId()));
            if ("I".equals(routeRuleStatus)) {
                logger.info("Route RuleId{} status is {}, The Rules have not yet entered into force.",
                        rule.getRuleId(), "I");
                // 校验是否开始生效
                if (rule.getRuleBaseInfo().getValidateTime().after(new Timestamp(System.currentTimeMillis()))) {
                    continue;
                } else {
                    //重置状态
                    rule.reloadData();
                }
            } else if ("U".equals(routeRuleStatus)) {
                logger.info("Route RuleId{} status is {}, This route cannot be match.",
                        rule.getRuleId(), "N");
                result = true;
                break;
            }

            boolean loadDataTag = rule.loadRuleData(rule.getRuleId(), routeRuleStatus);
            if (!loadDataTag) {
                logger.info("Cannot to load the ruleId[{}] data. cause by: {} time type is self-defined, and current date is out of the invalidate date",
                        rule.getRuleId(), rule.getRuleId());
                result = true;
                break;
            }

            Float testValue = rule.getRuleBaseInfo().getRuleType().fetchTestValue(dataMap);
            if (!rule.match(testValue)) {
                result = true;
                break;
            } else {
                hasBeenIncrement.put(CacheKeyUtil.RK_RouteRuleData(rule.getRuleId(), rule.getRuleBaseInfo().getRuleType()), testValue);
            }
        }

        if (result) {
            // 加入不匹配，之前通过的数据都需要被回滚
            for (Map.Entry<String, Float> entry : hasBeenIncrement.entrySet()) {
                MCSUtil.atomDecrement(entry.getKey(), entry.getValue());
            }

            logger.warn("RuleId don't match value, to be roll back previous date");
        }

        return result;
    }
}
