package com.ai.slp.route.cache.dto;

import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.util.MCSUtil;
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
        String routeGroupRedisKey = RedisKeyConfig.RK_RouteGroup(tenantId, routeGroupId);
        // 优先级和路由组ID
        Map<String, String> priorityRouteMapping = new HashMap<String, String>();
        for (PriorityRoutesMapping mapping : priorityRoutesMappings) {
            priorityRouteMapping.put(mapping.getPriorityNumber(), mapping.appendAllRouteIds());
            mapping.refreshAllRoutesCache();
        }

        logger.debug("Refresh key : {}, refresh Value: {}", routeGroupRedisKey, priorityRouteMapping);
        MCSUtil.expire(routeGroupRedisKey);
        MCSUtil.hput(routeGroupRedisKey, priorityRouteMapping);

        MCSUtil.expire(RedisKeyConfig.RK_RouteGroupStatus(tenantId, routeGroupId));
        MCSUtil.put(RedisKeyConfig.RK_RouteGroupStatus(tenantId, routeGroupId),
                groupStatus.getValue());
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
