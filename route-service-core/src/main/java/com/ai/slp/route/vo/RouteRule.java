package com.ai.slp.route.vo;

import com.ai.opt.sdk.util.DateUtil;
import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Created by xin on 16-4-29.
 */
public class RouteRule {

    private Logger logger = LoggerFactory.getLogger(RouteRule.class);

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

    public RouteRule(String routeId, String ruleId, String state,String baseInfo){
        this.ruleId = ruleId;
        this.routeId = routeId;
        this.ruleBaseInfo = new Gson().fromJson(baseInfo, RuleBaseInfo.class);
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
        String routeRuleKey = CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem());
        //规则量设置为零
        String previousValue = MCSUtil.load(routeRuleKey);
        MCSUtil.expire(routeRuleKey);
        MCSUtil.put(routeRuleKey, "0", ruleBaseInfo.getInvalidateTime().getTime() / 1000);
        String currentValue = MCSUtil.load(routeRuleKey);
        logger.info("Change RK:{} value:{} to {} ", routeRuleKey,previousValue, currentValue);

        //规则状态
        String routeStatusKey = CacheKeyUtil.RK_RouteRuleStatus(ruleId);
        previousValue = MCSUtil.load(routeStatusKey);
        MCSUtil.expire(routeStatusKey);
        MCSUtil.put(routeStatusKey, ruleStatus.getValue());
        currentValue = MCSUtil.load(routeRuleKey);
        logger.info("Change RK:{} value:{} to {} ", routeStatusKey,previousValue, currentValue);

        boolean result = true;
        //查询当前路由下所有的规则标识
        Set<String> ruleIds = MCSUtil.hLoads(CacheKeyUtil.RK_Route(routeId)).keySet();
        for (String ruleId : ruleIds) {
            if (ruleId.equals(this.ruleId)) {
                continue;
            }
            //存在无效状态
            String ruleStatus = MCSUtil.load(routeStatusKey);
            if (!RuleStatus.VALIDATE.getValue().equals(ruleStatus)) {
                result = false;
                break;
            }
        }
        //若所有规则均有效,则设置路由状态为有效
        if (result) {
            MCSUtil.put(CacheKeyUtil.RK_RouteStatus(routeId), Route.RouteStatus.VALIDATE.getValue());
        }
    }

    public boolean loadRuleData(String routeRuleId, String routeRuleStatus) {
        //获取路由数据
        String routeRuleData = MCSUtil.load(CacheKeyUtil.RK_RouteRuleData(routeRuleId, ruleBaseInfo.getRuleItem()));
        //如果路由规则信息为空
        if (routeRuleData == null) {
            // 如果规则类型是自定义
            if (ruleBaseInfo.getTimeType() == TimeType.SELF_DEFINED) {
                // 则将当前这个规则设置为失效了
                logger.warn("RouteRuleId[{}] has been invalidate. change status to [{}]",
                        routeRuleId, RuleStatus.INVALIDATE.name());
                MCSUtil.put(CacheKeyUtil.RK_RouteRuleStatus(routeRuleId), RuleStatus.INVALIDATE.getValue());
                return false;
            } else {
                logger.info("RouteRuleId[{}] has been invalidate, will reload data", routeRuleId);
                //根据基础信息重新生成
                reloadData();
            }
        }//如果为重新加载,则返回false
        else if (RuleStatus.RELOADING.getValue().equals(routeRuleStatus)){
            return false;
        }

        return true;
    }

    public boolean match(float value) {
        boolean result = true;
        double resultValue = MCSUtil.atomIncrement(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
        if (ruleBaseInfo.getMinQuantity() == -1) {
            if (resultValue > ruleBaseInfo.getMaxQuantity()) {
                result = false;
                // 更新值
                MCSUtil.atomDecrement(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
            } else if (resultValue == ruleBaseInfo.getMaxQuantity()) {
                //更新status
                logger.info("{} = {} Update RK[{}] status to {}", resultValue,
                        ruleBaseInfo.getMaxQuantity(), CacheKeyUtil.RK_RouteRuleStatus(ruleId), RuleStatus.INVALIDATE.name());
                setRouteRuleStatus();
            }
        } else {
            if (resultValue > ruleBaseInfo.getMaxQuantity()) {
                MCSUtil.atomDecrement(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
                result = false;
            } else if (resultValue <= ruleBaseInfo.getMaxQuantity() && resultValue >= ruleBaseInfo.getMinQuantity()) {
                //更新status
                logger.info("{} in [{},{}] Update RK[{}] status to {}", resultValue, ruleBaseInfo.getMinQuantity(),
                        ruleBaseInfo.getMaxQuantity(),
                        CacheKeyUtil.RK_RouteRuleStatus(ruleId), RuleStatus.INVALIDATE.name());
                setRouteRuleStatus();
            }
        }

        if (result) {
            logger.info("RuleId[{}]  match value [{}], current data:{}", ruleId, ruleBaseInfo.getRuleItem(), resultValue);
        } else {
            logger.info("RuleId[{}] don't match value [{}], current data:{}", ruleId, ruleBaseInfo.getRuleItem(), resultValue);
        }

        return result;
    }

    private void setRouteRuleStatus() {
        boolean isReloading = ruleBaseInfo.getTimeType() == TimeType.CYCLE;
        MCSUtil.put(CacheKeyUtil.RK_RouteRuleStatus(ruleId),
                isReloading?RuleStatus.RELOADING.getValue():RuleStatus.INVALIDATE.getValue());
    }

    public void reloadData() {
        if (ruleBaseInfo == null)
            return;
        Timestamp nextInvalidateTime = ruleBaseInfo.getInvalidateTime();
        if (ruleBaseInfo.getTimeType() == TimeType.CYCLE) {
            nextInvalidateTime = ruleBaseInfo.generateNextInvalidateTime();
        }
        //在当前时间之后
        if (nextInvalidateTime.after(DateUtil.getSysDate())) {
            String routeStatus = CacheKeyUtil.RK_RouteRuleStatus(ruleId);
            //直接更新值，当前值失效，然后只为0，置为失效时间
            MCSUtil.expire(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));
            logger.info("Reload rule date, RK[{}] next Invalidate time {}", routeStatus, nextInvalidateTime);
            MCSUtil.putnx(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), "0", nextInvalidateTime.getTime() / 1000);
            // 更新路由规则状态
            MCSUtil.put(routeStatus, RuleStatus.VALIDATE.getValue());
        }
    }

    /**
     * 规则状态
     */
    public enum RuleStatus {
        VALIDATE("1"), INVALIDATE("0"), INEFFECTIVE("-1"),RELOADING("-2");

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
                case "-2":{
                    return RELOADING;
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
