package com.ai.slp.route.core;

import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
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

    /**
     * 获取路由组信息
     * @param tenantId
     * @param routeGroupId
     * @return
     */
    public static RouteGroup load(String tenantId, String routeGroupId) {
        String routeGroupStatus = MCSUtil.load(CacheKeyUtil.RK_RouteGroupStatus(tenantId, routeGroupId));
        //若路由组状态为无效
        if (!"N".equals(routeGroupStatus)) {
            logger.warn("tenantId:{}  routeGroupId:{}  status:{}.", tenantId, routeGroupId, routeGroupStatus);
            return null;
        }
        //查询路由组下优先级中路由组成
        Map<String, String> priorityRouteMapping = MCSUtil.hLoads(CacheKeyUtil.RK_RouteGroup(tenantId, routeGroupId));
        return new RouteGroup(priorityRouteMapping);
    }

    public List<PriorityRoutesMapping> getPriorityRouteMapping() {
        return priorityRouteMapping;
    }
}
