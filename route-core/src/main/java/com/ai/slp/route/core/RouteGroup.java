package com.ai.slp.route.core;

import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.util.MCSUtil;
import com.ai.slp.route.common.util.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by xin on 16-4-27.
 */
public class RouteGroup {
    private static Logger logger = LogManager.getLogger(RouteGroup.class);

    private List<PriorityRoutesMapping> priorityRouteMapping;

    private RouteGroup(Map<String, String> priorityRouteMap) {
        List<String> priorities = new ArrayList<String>(priorityRouteMap.keySet());
        Collections.sort(priorities);
        priorityRouteMapping = new ArrayList<PriorityRoutesMapping>();
        for (String priority : priorities) {
            priorityRouteMapping.add(new PriorityRoutesMapping(priority, priorityRouteMap.get(priority)));
        }
    }

    public static RouteGroup load(String tenantId, String routeGroupId) {
        String routeGroupStatus = MCSUtil.load(RedisKeyConfig.RK_RouteGroupStatus(tenantId, routeGroupId));
        if (!"N".equals(routeGroupStatus)) {
            logger.warn("tenantId:{}  routeGroupId:{}  status:{}.", tenantId, routeGroupId, routeGroupStatus);
            return null;
        }

        Map<String, String> priorityRouteMapping = MCSUtil.hLoads(RedisKeyConfig.RK_RouteGroup(tenantId, routeGroupId));
        return new RouteGroup(priorityRouteMapping);
    }

    public List<PriorityRoutesMapping> getPriorityRouteMapping() {
        return priorityRouteMapping;
    }
}
