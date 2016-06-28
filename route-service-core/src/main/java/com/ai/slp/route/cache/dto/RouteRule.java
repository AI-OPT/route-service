package com.ai.slp.route.cache.dto;

import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.ai.slp.route.vo.RuleBaseInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Created by xin on 16-4-29.
 */
public class RouteRule {

    private Logger logger = LogManager.getLogger(RouteRule.class);

    private String ruleId;
    private String routeId;
    private RuleBaseInfo ruleBaseInfo;
    private RuleStatus ruleStatus;

    public RouteRule(String routeId, String ruleId, String state, RuleBaseInfo ruleBaseInfo) {
        this.routeId = routeId;
        this.ruleId = ruleId;
        this.ruleBaseInfo = ruleBaseInfo;
        // 还需要校验时间，如果起始时间在现在时间之后，则是未生效状态
        if (this.ruleBaseInfo.getValidateTime().after(new Timestamp(System.currentTimeMillis()))) {
            this.ruleStatus = RuleStatus.INEFFECTIVE;
        } else {
            this.ruleStatus = RuleStatus.convert(state);
        }
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

        String previousvalue = MCSUtil.load(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleType()));
        MCSUtil.expire(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleType()));
        MCSUtil.put(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleType()), "0", ruleBaseInfo.getInvalidateTime().getTime() / 1000);
        String currentvalue = MCSUtil.load(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleType()));

        logger.info("Change RK:{} value:{} to {} ", CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleType()),
                previousvalue, currentvalue);

        previousvalue = MCSUtil.load(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleType()));
        MCSUtil.expire(CacheKeyUtil.RK_RouteRuleStatus(ruleId));
        MCSUtil.put(CacheKeyUtil.RK_RouteRuleStatus(ruleId), ruleStatus.getValue());
        currentvalue = MCSUtil.load(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleType()));

        logger.info("Change RK:{} value:{} to {} ", CacheKeyUtil.RK_RouteRuleStatus(ruleId),
                previousvalue, currentvalue);


        //
        boolean result = true;
        Set<String> ruleIds = MCSUtil.hLoads(CacheKeyUtil.RK_Route(routeId)).keySet();
        for (String ruleId : ruleIds) {
            if (ruleId.equals(this.ruleId)) {
                continue;
            }
            String ruleStatus = MCSUtil.load(CacheKeyUtil.RK_RouteRuleStatus(ruleId));
            if (!"N".equals(ruleStatus)) {
                result = false;
                break;
            }
        }

        if (result) {
            MCSUtil.put(CacheKeyUtil.RK_RouteStatus(routeId), Route.RouteStatus.VALIDATE.getValue());
        }
    }

    public enum RuleStatus {
        VALIDATE("N"), INVALIDATE("U"), INEFFECTIVE("I");

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
                case "-1": {
                    return INEFFECTIVE;
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
