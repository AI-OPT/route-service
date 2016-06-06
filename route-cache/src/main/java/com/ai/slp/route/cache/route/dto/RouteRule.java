package com.ai.slp.route.cache.route.dto;

import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.entity.RuleBaseInfo;
import com.ai.slp.route.common.util.MCSUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by xin on 16-4-29.
 */
public class RouteRule {

    private Logger logger = LogManager.getLogger(RouteRule.class);

    private String ruleId;
    private String routeId;
    private RuleBaseInfo ruleBaseInfo;
    private RuleStatus ruleStatus;

    public RouteRule(String routeId, String ruleId, String state) {
        this.routeId = routeId;
        this.ruleId = ruleId;
        this.ruleStatus = RuleStatus.convert(state);
    }

    public void setRuleBaseInfo(RuleBaseInfo ruleBaseInfo) {
        this.ruleBaseInfo = ruleBaseInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteRule routeRule = (RouteRule) o;

        return ruleId != null ? ruleId.equals(routeRule.ruleId) : routeRule.ruleId == null;

    }

    @Override
    public int hashCode() {
        return ruleId != null ? ruleId.hashCode() : 0;
    }

    public RuleBaseInfo getRuleBaseInfo() {
        return ruleBaseInfo;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void refreshCache() {

        String previousvalue = MCSUtil.load(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));
        MCSUtil.expire(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));
        MCSUtil.put(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), "0", ruleBaseInfo.getInvalidateTime().getTime());
        String currentvalue = MCSUtil.load(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));

        logger.info("Change RK:{} value:{} to {} ", RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()),
                previousvalue, currentvalue);

        previousvalue = MCSUtil.load(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));
        MCSUtil.expire(RedisKeyConfig.RK_RouteRuleStatus(ruleId));
        MCSUtil.put(RedisKeyConfig.RK_RouteRuleStatus(ruleId), ruleStatus.getValue());
        currentvalue = MCSUtil.load(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));

        logger.info("Change RK:{} value:{} to {} ", RedisKeyConfig.RK_RouteRuleStatus(ruleId),
                previousvalue, currentvalue);

    }

    public enum RuleStatus {
        VALIDATE("N"), INVALIDATE("U");

        private String value;

        RuleStatus(String value) {
            this.value = value;
        }

        public static RuleStatus convert(String state) {
            switch (state) {
                case "1": {
                    return VALIDATE;
                }
                case "0": {
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

    public String getRouteId() {
        return routeId;
    }
}
