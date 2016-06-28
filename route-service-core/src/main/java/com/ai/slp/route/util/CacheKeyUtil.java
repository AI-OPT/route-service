package com.ai.slp.route.util;

import com.ai.slp.route.vo.RuleType;

/**
 * Created by xin on 16-4-28.
 */
public class CacheKeyUtil {

    /**
     * Redis key：Route group
     * Hash类型，一级为优先级，二级为该优先级下对应的routeId
     * <p/>
     * tenantId-routeGroupId
     */
    public static String RK_RouteGroup(String tenantId, String routeGroupId) {
        return tenantId + "-" + routeGroupId;
    }

    /**
     * Redis key： Route group状态
     * 用来存放 route group的状态，正常状态：N
     * <p/>
     * tenantId-routeGroupId-STATUS
     */
    public static String RK_RouteGroupStatus(String tenantId, String routeGroupId) {
        return tenantId + "-" + routeGroupId + "-STATUS";
    }

    /**
     * Redis key： Route
     * 用来存放 route group的状态，正常状态：N
     * Hash类型，一级为route rule Id，二级为route rule的基础配置信息(规则项，最大区间，生失效时间)
     * <p/>
     * routeId
     */
    public static String RK_Route(String routeId) {
        return routeId;
    }

    /**
     * Redis key： Route 状态
     * 用来存放 Route的状态，正常状态：N,
     * <p/>
     * routeId-STATUS
     */
    public static String RK_RouteStatus(String routeId) {
        return routeId + "-STATUS";
    }

    /**
     * Redis key： Route rule 当前量
     * <p/>
     * ruleId-DATA
     */
    public static String RK_RouteRuleData(String ruleId, RuleType ruleItem) {
        return ruleId + "-" + ruleItem + "-DATA";
    }

    /**
     * Redis key： Route rule状态
     * 用来存放 Route rule的状态，正常状态：N, 重新加载：RELOAD（需要重新计算rule的规则值）
     * <p/>
     * ruleId-STATUS
     */
    public static String RK_RouteRuleStatus(String ruleId) {
        return ruleId + "-STATUS";
    }

    /**
     * Redis key： Route Server 数据
     * 用来存放 Route Server的数据
     * <p/>
     * routeServerId-DATA
     */
    public static String RK_RouteServerData(String routeServerId) {
        return routeServerId + "-DATA";
    }
}
