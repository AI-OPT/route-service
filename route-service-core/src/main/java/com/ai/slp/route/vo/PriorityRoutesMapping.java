package com.ai.slp.route.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由组的优先级和路由对应信息
 * Created by xin on 16-4-29.
 */
public class PriorityRoutesMapping {
    private Logger logger = LoggerFactory.getLogger(PriorityRoutesMapping.class);
    private String priorityNumber;
    private List<Route> routeList;

    public PriorityRoutesMapping(String priorityNumber) {
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
        return this.priorityNumber;
    }

    /**
     * 获取路由id的字符串,以逗号(,)进行分隔
     * @return
     */
    public String appendAllRouteIds() {
        StringBuilder routedIds = new StringBuilder();
        for (Route route : routeList) {
            routedIds.append(route.getRouteId() + ",");
        }

        if (routedIds.length() > 0) {
            routedIds.deleteCharAt(routedIds.length() - 1);
        }

        return routedIds.toString();
    }

    public void refreshAllRoutesCache() {
        for (Route route : routeList) {
            // 路由要是没有规则，则不需要加入到Redis中
            route.refreshCache();
        }
    }
}
