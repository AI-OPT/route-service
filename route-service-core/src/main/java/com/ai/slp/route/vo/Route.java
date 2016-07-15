package com.ai.slp.route.vo;

import com.ai.opt.sdk.util.DateUtil;
import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
        this.routeStatus = RouteStatus.convert(state);
        this.routeRules = new ArrayList<RouteRule>();
    }

    public Route(String routeId,String state, Map<String, String> routeRulesMapping) {
        this(routeId,state);
        //加载路由规则
        for (Map.Entry<String, String> entry : routeRulesMapping.entrySet()) {
            //获取路由规则状态
            String routeStatus = MCSUtil.load(CacheKeyUtil.RK_RouteRuleStatus(entry.getKey()));
            //路由中添加路由规则
            this.routeRules.add(new RouteRule(routeId,entry.getKey(),routeStatus, entry.getValue()));
        }
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteRules(List<RouteRule> routeRules) {
        this.routeRules.addAll(routeRules);
    }

    /**
     * 刷新路由相关缓存
     */
    public void refreshCache() {
        //刷新路由规则缓存
        refreshRouteRuleData();
        //刷新路由缓存
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
        //设置路由下规则
        Map<String, String> ruleBaseInfoMap = new HashMap<String, String>();
        for (RouteRule routeRule : routeRules) {
            ruleBaseInfoMap.put(routeRule.getRuleId(), new Gson().toJson(routeRule.getRuleBaseInfo()));
        }
        String routeDataKey = CacheKeyUtil.RK_Route(routeId);
        MCSUtil.expire(routeDataKey);
        MCSUtil.hput(routeDataKey, ruleBaseInfoMap);
        logger.debug("Refresh key:{}, Refsh Value:{}", routeDataKey, ruleBaseInfoMap);

        //设置路由状态
        String routeStatusKey = CacheKeyUtil.RK_RouteStatus(routeId);
        MCSUtil.expire(routeStatusKey);
        MCSUtil.put(routeStatusKey, routeStatus.getValue());
        logger.debug("Refresh key:{}, Refsh Value:{}", routeStatusKey, routeStatus.getValue());
    }

    /**
     * 根据路由id从缓存中的获取路由信息
     * @param routeId
     * @return
     */
    public static Route load(String routeId) {
        //获取路由状态
        String routeStatus = MCSUtil.load(CacheKeyUtil.RK_RouteStatus(routeId));
        // 如果路由不是有效状态,则返回null
        if (!RouteStatus.VALIDATE.getValue().equals(routeStatus)) {
            return null;
        }
        //获取路由规则信息
        Map<String, String> routeRulesMapping = MCSUtil.hLoads(CacheKeyUtil.RK_Route(routeId));
        //返回路由
        return new Route(routeId,routeStatus, routeRulesMapping);
    }

    /**
     * 判断路由是否符合使用条件
     * @param dataJson
     * @return
     */
    public boolean isOutOfRules(String dataJson) {
        Map<String, Float> hasBeenIncrement = new HashMap<String, Float>();
        Map<String, Float> dataMap = new Gson().fromJson(dataJson,
                new TypeToken<Map<String, Float>>() {}.getType());

        boolean result = false;
        //进行规则判断
        for (RouteRule rule : routeRules) {
            //获取规则状态
            String routeRuleStatus = MCSUtil.load(CacheKeyUtil.RK_RouteRuleStatus(rule.getRuleId()));
            //若规则为待生效,则检查是否可以生效
            if (RouteRule.RuleStatus.INEFFECTIVE.getValue().equals(routeRuleStatus)) {
                logger.info("Route RuleId{} status is {}, The Rules have not yet entered into force.",
                        rule.getRuleId(),routeRuleStatus);
                // 未到生效时间,则查询下一个规则
                if (rule.getRuleBaseInfo().getValidateTime().after(DateUtil.getSysDate())) {
                    continue;
                }//可以生效,则进行状态变更
                else {
                    //重置状态
                    rule.reloadData();
                }
            } //若规则无效,表示当前路由无效
            else if (RouteRule.RuleStatus.INVALIDATE.getValue().equals(routeRuleStatus)) {
                logger.info("Route RuleId{} status is {}, This route cannot be match.",
                        rule.getRuleId(), routeRuleStatus);
                result = true;
                break;
            }

            boolean loadDataTag = rule.loadRuleData(rule.getRuleId(), routeRuleStatus);
            if (!loadDataTag) {
                logger.info("Cannot to load the ruleId[{}] data. " +
                        "cause by: {} time type is self-defined, and current date is out of the invalidate date",
                        rule.getRuleId(), rule.getRuleId());
                result = true;
                break;
            }
            //获得对应消耗量
            Float testValue = rule.getRuleBaseInfo().getRuleItem().fetchTestValue(dataMap);
            //如果有一条规则无效,则路由也是无效
            if (!rule.match(testValue)) {
                result = true;
                break;
            }//添加路由使用量
            else {
                hasBeenIncrement.put(
                        CacheKeyUtil.RK_RouteRuleData(rule.getRuleId(),rule.getRuleBaseInfo().getRuleItem()),
                        testValue);
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

    public enum RouteStatus {
        VALIDATE("2"), INVALIDATE("21");

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
