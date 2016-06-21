package com.ai.slp.route.core;

import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.util.MCSUtil;
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
        String routeStatus = MCSUtil.load(RedisKeyConfig.RK_RouteStatus(routeId));
        // 先判断状态
        if (!"N".equals(routeStatus)) {
            return null;
        }
        //
        Map<String, String> routeRulesMapping = MCSUtil.hLoads(RedisKeyConfig.RK_Route(routeId));
        return new Route(routeId, routeRulesMapping);
    }

    public boolean isOutOfRules(String dataJson) {
        Map<String, Float> hasBeenIncrement = new HashMap<String, Float>();
        Map<String, Float> dataMap = new Gson().fromJson(dataJson,
                new TypeToken<Map<String, Float>>() {
                }.getType());

        for (RouteRule rule : ruleIds) {
            String routeRuleStatus = MCSUtil.load(RedisKeyConfig.RK_RouteRuleStatus(rule.getRuleId()));
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
                return true;
            }

            boolean loadDataTag = rule.loadRuleData(rule.getRuleId(), routeRuleStatus);
            if (!loadDataTag) {
                logger.info("Cannot to load the ruleId[{}] data. cause by: {} time type is self-defined, and current date is out of the invalidate date",
                        rule.getRuleId(), rule.getRuleId());
                return true;
            }

            Float testValue = rule.getRuleBaseInfo().getRuleItem().fetchTestValue(dataMap);
            if (!rule.match(testValue)) {
                // 加入不匹配，之前通过的数据都需要被回滚
                for (Map.Entry<String, Float> entry : hasBeenIncrement.entrySet()) {
                    MCSUtil.atomDecrement(entry.getKey(), entry.getValue());
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
