package com.ai.slp.route.core;

import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.entity.RuleBaseInfo;
import com.ai.slp.route.common.entity.TimeType;
import com.ai.slp.route.common.util.RedisUtil;
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

    public boolean loadRuleData(String routeRuleId) {
        String routeRuleData = RedisUtil.load(RedisKeyConfig.RK_RouteRuleData(routeRuleId, ruleBaseInfo.getRuleItem()));
        if (routeRuleData == null) {
            //如果为空，有两种情况，需要重新加载数据
            if (ruleBaseInfo.getTimeType() == TimeType.SELF_DEFINED) {
                // 如果当前是自定义的，则将当前这个规则
                // 当前这个规则失效了
                logger.warn("RouteRuleId[{}] has been invalidate. change status to [{}]", routeRuleId, "INVALIDATE");
                RedisUtil.put(RedisKeyConfig.RK_RouteRuleStatus(routeRuleId), "INVALIDATE");
                return false;
            } else {
                logger.info("RouteRuleId[{}] has been invalidate, will reload data", routeRuleId);
                //根据基础信息重新生成
                reloadData();
            }
        }

        return true;
    }

    public boolean match(float value) {
        boolean result = true;
        double resultValue = RedisUtil.atomIncrement(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
        if (ruleBaseInfo.getMinQuantity() == -1) {
            if (resultValue > ruleBaseInfo.getMaxQuantity()) {
                result = false;
                // 更新值
                RedisUtil.atomDecrement(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
            } else if (resultValue == ruleBaseInfo.getMaxQuantity()) {
                //更新status
                logger.info("{} = {} Update RK[{}] status to {}", resultValue,
                        ruleBaseInfo.getMaxQuantity(), RedisKeyConfig.RK_RouteRuleStatus(ruleId), "INVALIDATE");
                RedisUtil.put(RedisKeyConfig.RK_RouteRuleStatus(ruleId), "U");
            }
        } else {
            if (resultValue > ruleBaseInfo.getMaxQuantity()) {
                RedisUtil.atomDecrement(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), value);
                result = false;
            } else if (resultValue <= ruleBaseInfo.getMaxQuantity() && resultValue >= ruleBaseInfo.getMinQuantity()) {
                //更新status
                logger.info("{} in [{},{}] Update RK[{}] status to {}", resultValue, ruleBaseInfo.getMinQuantity(),
                        ruleBaseInfo.getMaxQuantity(),
                        RedisKeyConfig.RK_RouteRuleStatus(ruleId), "INVALIDATE");
                RedisUtil.put(RedisKeyConfig.RK_RouteRuleStatus(ruleId), "U");
            }
        }

        if (result) {
            logger.info("RuleId[{}]  match value [{}], current data:{}", ruleId, ruleBaseInfo.getRuleItem(), resultValue);
        } else {
            logger.info("RuleId[{}] don't match value [{}], current data:{}", ruleId, ruleBaseInfo.getRuleItem(), resultValue);
        }

        return result;
    }

    public void reloadData() {
        //
        if (ruleBaseInfo != null) {
            //直接更新值，当前值失效，然后只为0，置为失效时间
            RedisUtil.expire(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));
            Timestamp nextInvalidateTime = ruleBaseInfo.generateNextInvalidateTime();
            logger.info("Reload rule date, RK[{}] next Invalidate time {}", RedisKeyConfig.RK_RouteRuleStatus(ruleId), nextInvalidateTime);
            RedisUtil.putnx(RedisKeyConfig.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), "0", nextInvalidateTime.getTime());
        }
    }

    public RuleBaseInfo getRuleBaseInfo() {
        return ruleBaseInfo;
    }
}
