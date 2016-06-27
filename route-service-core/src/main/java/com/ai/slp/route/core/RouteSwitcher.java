package com.ai.slp.route.core;

public class RouteSwitcher {
    public static Route switchRoute(RouteGroup routeGroup, String dataJson) {
        Route route = null;
        for (PriorityRoutesMapping priorityRoutesMapping : routeGroup.getPriorityRouteMapping()) {
            route = priorityRoutesMapping.switchRoute(dataJson);
            if (route != null) {
                break;
            }
        }
        return route;
    }
}
