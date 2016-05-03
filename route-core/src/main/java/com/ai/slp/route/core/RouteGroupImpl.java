package com.ai.slp.route.core;


public class RouteGroupImpl implements IRouteGroup {
    @Override
    public String switchRoute(String tenantId, String groupId, String dataJson) {
        RouteGroup routeGroup = RouteGroup.load(tenantId, groupId);
        if (routeGroup == null) {
            throw new RuntimeException("Failed to find the tenantId:" + tenantId + " groupId:" + groupId);
        }

        Route result = RouteSwitcher.switchRoute(routeGroup, dataJson);

        if (result != null) {
            return result.getRouteId();
        }
        return null;
    }
}
