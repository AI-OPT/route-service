package com.ai.slp.route.core;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RouteSwitcherImpl implements IRouteSwitcher {

    private Logger logger = LogManager.getLogger(RouteSwitcherImpl.class);

    @Override
    public Route switchRoute(String tenantId, String groupId, String dataJson) {
        RouteGroup routeGroup = RouteGroup.load(tenantId, groupId);
        if (routeGroup == null) {
            logger.error("Failed to find the tenantId:{}, groupId:{}", tenantId, groupId);
            return null;
        }

        Route result = RouteSwitcher.switchRoute(routeGroup, dataJson);

        if (result != null) {
            return result;
        }
        return null;
    }
}
