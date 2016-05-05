package com.ai.slp.route.core;


public class RouteSwitcherImpl implements IRouteSwitcher {
    @Override
    public Route switchRoute(String tenantId, String groupId, String dataJson) {
        RouteGroup routeGroup = RouteGroup.load(tenantId, groupId);
        if (routeGroup == null) {
            throw new RuntimeException("Failed to find the tenantId:" + tenantId + " groupId:" + groupId);
        }

        Route result = RouteSwitcher.switchRoute(routeGroup, dataJson);

        if (result != null) {
            return result;
        }
        return null;
    }
}
