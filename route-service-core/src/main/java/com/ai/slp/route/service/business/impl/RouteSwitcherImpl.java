package com.ai.slp.route.service.business.impl;

import com.ai.slp.route.core.PriorityRoutesMapping;
import com.ai.slp.route.core.Route;
import com.ai.slp.route.core.RouteGroup;
import com.ai.slp.route.service.business.interfaces.IRouteSwitcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RouteSwitcherImpl implements IRouteSwitcher {

    private Logger logger = LogManager.getLogger(RouteSwitcherImpl.class);

    @Override
    public Route switchRoute(String tenantId, String groupId, String dataJson) {
        Route route = null;
        RouteGroup routeGroup = RouteGroup.load(tenantId, groupId);
        if (routeGroup != null) {
            route = switchRoute(routeGroup, dataJson);
        }else
            logger.error("Failed to find the route group, tenantId:{}, groupId:{}", tenantId, groupId);

        return route;
    }

    private Route switchRoute(RouteGroup routeGroup, String dataJson) {
        Route route = null;
        //获取路由组中优先级对应路由,逐级检查是否符合条件
        for (PriorityRoutesMapping priorityRoutesMapping : routeGroup.getPriorityRouteMapping()) {
            route = priorityRoutesMapping.switchRoute(dataJson);
            if (route != null) {
                break;
            }
        }
        return route;
    }
}
