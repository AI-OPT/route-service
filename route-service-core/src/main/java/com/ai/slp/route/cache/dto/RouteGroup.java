package com.ai.slp.route.cache.dto;

import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteGroup {
    private Logger logger = LogManager.getLogger(RouteGroup.class);
    //
    private String tenantId;
    private String routeGroupName;
    private String routeGroupId;
    private RouteGroupStatus groupStatus;

    private List<PriorityRoutesMapping> priorityRoutesMappings;

    public RouteGroup(String tenantId, String routeGroupId, String routeGroupName) {
        this.tenantId = tenantId;
        this.routeGroupName = routeGroupName;
        this.routeGroupId = routeGroupId;
    }

    public List<PriorityRoutesMapping> getPriorityRoutesMappings() {
        return priorityRoutesMappings;
    }

    public String getRouteGroupId() {
        return routeGroupId;
    }

    public void addPriorityMapping(List<PriorityRoutesMapping> routesMappings) {
        this.priorityRoutesMappings = routesMappings;
    }

    public void setGroupStatus(RouteGroupStatus groupStatus) {
        this.groupStatus = groupStatus;
    }

    public void refreshCache() {
        // 优先级和路由组ID
        Map<String, String> priorityRouteMapping = new HashMap<String, String>();
        for (PriorityRoutesMapping mapping : priorityRoutesMappings) {
            //获取优先级对应路由标识集合
            priorityRouteMapping.put(mapping.getPriorityNumber(), mapping.appendAllRouteIds());
            //进行路由相关信息刷新
            mapping.refreshAllRoutesCache();
        }
        //A
        String routeGroupRedisKey = CacheKeyUtil.RK_RouteGroup(tenantId, routeGroupId);
        logger.debug("Refresh key : {}, refresh Value: {}", routeGroupRedisKey, priorityRouteMapping);
        //设置缓存key失效
        MCSUtil.expire(routeGroupRedisKey);
        //路由组优先级对应路由标识
        MCSUtil.hput(routeGroupRedisKey, priorityRouteMapping);

        //B
        String routeGroupStatusKey = CacheKeyUtil.RK_RouteGroupStatus(tenantId, routeGroupId);
        //设置缓存key失效
        MCSUtil.expire(routeGroupStatusKey);
        //路由组状态
        MCSUtil.put(routeGroupStatusKey,groupStatus.getValue());
    }

    public enum RouteGroupStatus {
        VALIDATE("N"), INVALIDATE("U");

        private final String value;

        RouteGroupStatus(String value) {
            this.value = value;
        }

        public static RouteGroupStatus convert(String state) {
            switch (state) {
                case "2":
                    return VALIDATE;
                case "21":
                    return INVALIDATE;
                default:
                    throw new RuntimeException("Can not find the type[" + state + "]");
            }
        }

        public String getValue() {
            return value;
        }
    }
}
