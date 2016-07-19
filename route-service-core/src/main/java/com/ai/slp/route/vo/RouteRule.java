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
        this.ruleStatus = RuleStatus.convert(state);
    }

    public RouteRule(String routeId, String ruleId, String state,String baseInfo){
        this(routeId,ruleId,state,new Gson().fromJson(baseInfo, RuleBaseInfo.class));
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

    /**
     * 刷新路由规则缓存
     */
    public void refreshCache() {
        //规则下已使用量
        String routeRuleKey = CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem());
        String previousValue = MCSUtil.load(routeRuleKey);
        MCSUtil.expire(routeRuleKey);
        //规则量设置为零,设置规则量的有效期
        MCSUtil.put(routeRuleKey, "0", ruleBaseInfo.getInvalidateTime().getTime() / 1000);
        String currentValue = MCSUtil.load(routeRuleKey);
        logger.info("Change RK:{} value:{} to {} ", routeRuleKey,previousValue, currentValue);

        //规则状态
        String ruleStatusKey = CacheKeyUtil.RK_RouteRuleStatus(ruleId);
        previousValue = MCSUtil.load(ruleStatusKey);
        MCSUtil.expire(ruleStatusKey);
        MCSUtil.put(ruleStatusKey, ruleStatus.getValue());
        currentValue = MCSUtil.load(routeRuleKey);
        logger.info("Change RK:{} value:{} to {} ", ruleStatusKey,previousValue, currentValue);

        boolean result = true;
        //查询当前路由下所有的规则标识
        Set<String> ruleIds = MCSUtil.hLoads(CacheKeyUtil.RK_Route(routeId)).keySet();
        for (String ruleId : ruleIds) {
            if (ruleId.equals(this.ruleId)) {
                continue;
            }
            //存在无效状态
            String ruleStatus = MCSUtil.load(CacheKeyUtil.RK_RouteRuleStatus(ruleId));
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
        boolean loadSuccess = true;
        //获取路由数据
        String routeRuleData = MCSUtil.load(CacheKeyUtil.RK_RouteRuleData(routeRuleId, ruleBaseInfo.getRuleItem()));
        //如果路由规则信息为空
        if (routeRuleData == null) {
            // 如果规则类型是自定义,则设置当前规则失效
            if (ruleBaseInfo.getTimeType() == TimeType.SELF_DEFINED) {
                // 则将当前这个规则设置为失效了
                logger.warn("RouteRuleId[{}] has been invalidate. change status to [{}]",
                        routeRuleId, RuleStatus.INVALIDATE.name());
                MCSUtil.put(CacheKeyUtil.RK_RouteRuleStatus(routeRuleId), RuleStatus.INVALIDATE.getValue());
                loadSuccess = false;
            } //规则类型为周期性,则重新加载规则信息
            else {
                logger.info("RouteRuleId[{}] has been invalidate, will reload data", routeRuleId);
                //根据基础信息重新生成
                reloadData();
            }
        }//如果为重新加载,则返回false
        else if (RuleStatus.RELOADING.getValue().equals(routeRuleStatus)){
            loadSuccess = false;
        }

        return loadSuccess;
    }

    /**
     * 进行规则消耗量匹配
     * @param value
     * @return
     */
    public boolean match(float value) {
        boolean result = true;
        String ruleDataKey = CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem());
        //添加消耗量
        double resultValue = MCSUtil.atomIncrement(ruleDataKey, value);
        logger.info("RK[{}] result is {}",ruleDataKey,resultValue);
        //如果规则没有最小限制
        if (ruleBaseInfo.getMinQuantity() == -1) {
            //如果大于消耗量,则进行消耗量回退,并返回规则不匹配
            if (resultValue > ruleBaseInfo.getMaxQuantity()) {
                result = false;
                // 回退使用量
                MCSUtil.atomDecrement(ruleDataKey, value);
            }//若等于最大量,则将规则状态重新设置
            else if (resultValue == ruleBaseInfo.getMaxQuantity()) {
                //更新status
                logger.info("{} = {} Update RK[{}] status to {}", resultValue,
                        ruleBaseInfo.getMaxQuantity(), CacheKeyUtil.RK_RouteRuleStatus(ruleId), RuleStatus.INVALIDATE.name());
                setRouteRuleStatus();
            }
        } else {
            //若大于最大量,则进行回退
            if (resultValue > ruleBaseInfo.getMaxQuantity()) {
                //进行使用量回退
                MCSUtil.atomDecrement(ruleDataKey, value);
                result = false;
            }//使用量大于最小值,小于最大值,则重新设置状态
            else if (resultValue <= ruleBaseInfo.getMaxQuantity() && resultValue >= ruleBaseInfo.getMinQuantity()) {
                //更新status
                logger.info("{} in [{},{}] Update RK[{}] status to {}", resultValue, ruleBaseInfo.getMinQuantity(),
                        ruleBaseInfo.getMaxQuantity(),
                        CacheKeyUtil.RK_RouteRuleStatus(ruleId), RuleStatus.INVALIDATE.name());
                setRouteRuleStatus();
            }
        }
        logger.info("RuleId[{}] {} match value [{}], current data:{}",
                ruleId, result?"":"don't", ruleBaseInfo.getRuleItem(), resultValue);
        return result;
    }

    /**
     * 重新设置路由规则状态
     */
    private void setRouteRuleStatus() {
        //若为周期性规则,则设置失效
        if (ruleBaseInfo.getTimeType() == TimeType.CYCLE){
            MCSUtil.put(CacheKeyUtil.RK_RouteRuleStatus(ruleId),RuleStatus.INVALIDATE.getValue());
        }
    }

    /**
     * 重载规则信息
     */
    public boolean reloadData() {
        boolean isReload = false;
        if (ruleBaseInfo == null)
            return isReload;
        //获得当前失效时间
        Timestamp nextInvalidateTime = ruleBaseInfo.getInvalidateTime();
        //若时段类型为周期性,查询下次失效时间
        if (ruleBaseInfo.getTimeType() == TimeType.CYCLE) {
            nextInvalidateTime = ruleBaseInfo.generateNextInvalidateTime();
        }
        //失效时间在当前时间之后
        if (nextInvalidateTime.after(DateUtil.getSysDate())) {
            //查询路由规则状态
            String routeStatus = CacheKeyUtil.RK_RouteRuleStatus(ruleId);
            //重置路由规则量
            MCSUtil.expire(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()));
            logger.info("Reload rule date, RK[{}] next Invalidate time {}", routeStatus, nextInvalidateTime);
            MCSUtil.putnx(CacheKeyUtil.RK_RouteRuleData(ruleId, ruleBaseInfo.getRuleItem()), "0", nextInvalidateTime.getTime() / 1000);
            // 更新路由规则状态为有效
            MCSUtil.put(routeStatus, RuleStatus.VALIDATE.getValue());
            isReload = true;
        }
        return isReload;
    }

    /**
     * 规则状态
     */
    public enum RuleStatus {
        VALIDATE("1"),//有效
        INVALIDATE("0"),//无效
        INEFFECTIVE("-1"),//未到生效时间,待生效,将废弃.
        RELOADING("-2");//重新加载

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
