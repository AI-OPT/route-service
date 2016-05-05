package com.ai.slp.route.core;

import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.util.RedisUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xin on 16-4-27.
 */
public class Route {
    private Logger logger = LogManager.getLogger(Route.class);
    private List<RouteRule> ruleIds;
    private String routeId;

    public String getRouteId() {
        return routeId;
    }

    public Route(String routeId, Map<String, String> routeRulesMapping) {
        this.routeId = routeId;
        ruleIds = new ArrayList<RouteRule>();
        for (Map.Entry<String, String> entry : routeRulesMapping.entrySet()) {
            ruleIds.add(new RouteRule(entry.getKey(), entry.getValue()));
        }
    }

    public static Route load(String routeId) {
        String routeStatus = RedisUtil.load(RedisKeyConfig.RK_RouteStatus(routeId));
        // 先判断状态
        if (!"N".equals(routeStatus)) {
            return null;
        }
        //
        Map<String, String> routeRulesMapping = RedisUtil.hLoads(RedisKeyConfig.RK_Route(routeId));
        return new Route(routeId, routeRulesMapping);
    }

    public boolean isOutOfRules(String dataJson) {
        Map<String, Float> hasBeenIncrement = new HashMap<String, Float>();
        Map<String, Float> dataMap = new Gson().fromJson(dataJson,
                new TypeToken<Map<String, Float>>() {
                }.getType());

        for (RouteRule rule : ruleIds) {
            String routeStatus = RedisUtil.load(RedisKeyConfig.RK_RouteRuleStatus(rule.getRuleId()));
            if (!"N".equals(routeStatus)) {
                logger.info("Route RuleId{} status is {}, This route cannot be match.",
                        rule.getRuleId(), "N");
                return true;
            } else if ("RELOAD".equals(routeStatus)) {
                //重新生成ruleData
                logger.info("Route RuleId{} status is {}, This route data need to be reload.",
                        rule.getRuleId(), "N");
                rule.reloadData();
            }


            boolean loadDataTag = rule.loadRuleData(rule.getRuleId());
            if (!loadDataTag) {
                logger.info("Cannot to load the ruleId[{}] data. cause by: {} time type is self-defined, and current date is out of the invalidate date",
                        rule.getRuleId(), rule.getRuleId());
                return true;
            }

            Float testValue = rule.getRuleBaseInfo().getRuleItem().fetchTestValue(dataMap);
            if (!rule.match(testValue)) {
                // 加入不匹配，之前通过的数据都需要被回滚
                for (Map.Entry<String, Float> entry : hasBeenIncrement.entrySet()) {
                    RedisUtil.atomDecrement(entry.getKey(), entry.getValue());
                }
                logger.warn("RuleId[{}] don't match value, to be roll back previous date", rule.getRuleId());
                return true;
            } else {
                hasBeenIncrement.put(RedisKeyConfig.RK_RouteRuleData(rule.getRuleId(), rule.getRuleBaseInfo().getRuleItem()), testValue);
            }
        }

        return false;
    }
}
