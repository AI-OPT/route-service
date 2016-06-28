package com.ai.slp.route.core;

import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.entity.RuleBaseInfo;
import com.ai.slp.route.common.entity.TimeType;
import com.ai.slp.route.util.MCSUtil;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;

public class RouteRule {

    private Logger logger = LogManager.getLogger(RouteRule.class);
    private String ruleId;
    private RuleBaseInfo ruleBaseInfo;


    public RouteRule(String ruleId, String baseInfo) {
        this.ruleId = ruleId;
        ruleBaseInfo = new Gson().fromJson(baseInfo, RuleBaseInfo.class);
    }


    public String getRuleId() {
        return ruleId;
    }

    public boolean loadRuleData(String routeRuleId, String routeRuleStatus) {
        String routeRuleData = MCSUtil.load(RedisKeyConfig.RK_RouteRuleData(routeRuleId, ruleBaseInfo.getRuleItem()));
        if (routeRuleData == null) {
            //如果为空，有两种情况，需要重新加载数据
            if (ruleBaseInfo.getTimeType() == TimeType.SELF_DEFINED) {
                // 如果当前是自定义的，则将当前这个规则
                // 当前这个规则失效了
                logger.warn("RouteRuleId[{}] has been invalidate. change status to [{}]", routeRuleId, "INVALIDATE");
                MCSUtil.put(RedisKeyConfig.RK_RouteRuleStatus(routeRuleId), "INVALIDATE");
                return false;
            } else {
                logger.info("RouteRuleId[{}] has been invalidate, will reload data", routeRuleId);
                //根据基础信息重新生成
                reloadData();
            }
        }else{
            if ("RELOADING".equals(routeRuleStatus)){
                return false;
            }
        }

        return true;
    }

    public boolean match(float value) {
        boolean result = true;
        double resultValue = MCSUtil.atomIncrement(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
        if (ruleBaseInfo.getMinQuantity() == -1) {
            if (resultValue > ruleBaseInfo.getMaxQuantity()) {
                result = false;
                // 更新值
                MCSUtil.atomDecrement(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
            } else if (resultValue == ruleBaseInfo.getMaxQuantity()) {
                //更新status
                logger.info("{} = {} Update RK[{}] status to {}", resultValue,
                        ruleBaseInfo.getMaxQuantity(), RedisKeyConfig.RK_RouteRuleStatus(ruleId), "INVALIDATE");
                setRouteRuleStatus();
            }
        } else {
            if (resultValue > ruleBaseInfo.getMaxQuantity()) {
                MCSUtil.atomDecrement(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
                result = false;
            } else if (resultValue <= ruleBaseInfo.getMaxQuantity() && resultValue >= ruleBaseInfo.getMinQuantity()) {
                //更新status
                logger.info("{} in [{},{}] Update RK[{}] status to {}", resultValue, ruleBaseInfo.getMinQuantity(),
                        ruleBaseInfo.getMaxQuantity(),
                        RedisKeyConfig.RK_RouteRuleStatus(ruleId), "INVALIDATE");
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
        if (ruleBaseInfo.getTimeType() == TimeType.CYCLE) {
            MCSUtil.put(RedisKeyConfig.RK_RouteRuleStatus(ruleId), "RELOADING");
        }else{
            MCSUtil.put(RedisKeyConfig.RK_RouteRuleStatus(ruleId), "U");
        }
    }

    public void reloadData() {
        //
        if (ruleBaseInfo != null) {
            Timestamp nextInvalidateTime = ruleBaseInfo.getInvalidateTime();
            if (ruleBaseInfo.getTimeType() == TimeType.CYCLE) {
                nextInvalidateTime = ruleBaseInfo.generateNextInvalidateTime();
            }
            if (nextInvalidateTime.after(new Timestamp(System.currentTimeMillis()))) {
                //直接更新值，当前值失效，然后只为0，置为失效时间
                MCSUtil.expire(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));
                logger.info("Reload rule date, RK[{}] next Invalidate time {}", RedisKeyConfig.RK_RouteRuleStatus(ruleId), nextInvalidateTime);
                MCSUtil.putnx(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), "0", nextInvalidateTime.getTime() / 1000);
                // 更新路由规则状态
                MCSUtil.put(RedisKeyConfig.RK_RouteRuleStatus(ruleId), "N");
            }
        }
    }

    public RuleBaseInfo getRuleBaseInfo() {
        return ruleBaseInfo;
    }
}
