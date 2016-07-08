package com.ai.slp.route.cache.dto;

import com.ai.opt.sdk.util.DateUtil;
import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.ai.slp.route.vo.RuleBaseInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        if (this.ruleBaseInfo.getValidateTime().after(DateUtil.getSysDate())) {
            this.ruleStatus = RuleStatus.INEFFECTIVE;
        } else {
            this.ruleStatus = RuleStatus.convert(state);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        String oRuleId = ((RouteRule) o).getRouteId();

        return ruleId != null ? ruleId.equals(oRuleId) : oRuleId == null;

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
        String routeRuleKey = CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleType());
        //规则量设置为零
        String previousValue = MCSUtil.load(routeRuleKey);
        MCSUtil.expire(routeRuleKey);
        MCSUtil.put(routeRuleKey, "0", ruleBaseInfo.getInvalidateTime().getTime() / 1000);
        String currentValue = MCSUtil.load(routeRuleKey);
        logger.info("Change RK:{} value:{} to {} ", routeRuleKey,previousValue, currentValue);

        //规则状态
        previousValue = MCSUtil.load(routeRuleKey);
        MCSUtil.expire(CacheKeyUtil.RK_RouteRuleStatus(ruleId));
        MCSUtil.put(CacheKeyUtil.RK_RouteRuleStatus(ruleId), ruleStatus.getValue());
        currentValue = MCSUtil.load(routeRuleKey);
        logger.info("Change RK:{} value:{} to {} ", CacheKeyUtil.RK_RouteRuleStatus(ruleId),
                previousValue, currentValue);

        boolean result = true;
        //查询当前路由下所有的规则标识
        Set<String> ruleIds = MCSUtil.hLoads(CacheKeyUtil.RK_Route(routeId)).keySet();
        for (String ruleId : ruleIds) {
            if (ruleId.equals(this.ruleId)) {
                continue;
            }
            //存在无效状态
            String ruleStatus = MCSUtil.load(CacheKeyUtil.RK_RouteRuleStatus(ruleId));
            if (!"N".equals(ruleStatus)) {
                result = false;
                break;
            }
        }
        //若所有规则均有效,则设置路由状态为有效
        if (result) {
            MCSUtil.put(CacheKeyUtil.RK_RouteStatus(routeId), Route.RouteStatus.VALIDATE.getValue());
        }
    }

    /**
     * 规则状态
     */
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
