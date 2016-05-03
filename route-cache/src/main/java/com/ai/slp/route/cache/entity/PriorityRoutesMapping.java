package com.ai.slp.route.cache.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xin on 16-4-29.
 */
public class PriorityRoutesMapping {
    private String RouteItemId;
    private String priorityNumber;

    private List<Route> routeList;

    public PriorityRoutesMapping(String routeItemId, String priorityNumber) {
        RouteItemId = routeItemId;
        this.priorityNumber = priorityNumber;
        this.routeList = new ArrayList<Route>();
    }

    public List<Route> getRouteList() {
        return routeList;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriorityRoutesMapping mapping = (PriorityRoutesMapping) o;
        return priorityNumber.equals(mapping.priorityNumber);

    }

    @Override
    public int hashCode() {
        return priorityNumber.hashCode();
    }

    public void addRoute(Route route) {
        this.routeList.add(route);
    }

    public String getPriorityNumber() {
        return priorityNumber;
    }

    public String appendAllRouteIds() {
        StringBuilder routedIds = new StringBuilder();
        for (Route route : routeList) {
            // 路由要是没有规则，则不需要加入到Redis中
            if (route.getRouteRules().size() != 0) {
                routedIds.append(route.getRouteId() + ",");
            }
        }
        routedIds.deleteCharAt(routedIds.length() - 1);
        return routedIds.toString();
    }

    public void refreshAllRoutesCache() {
        for (Route route : routeList) {
            // 路由要是没有规则，则不需要加入到Redis中
            if (route.getRouteRules().size() != 0) {
                route.refreshCache();
            }
        }
    }
}
