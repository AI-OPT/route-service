package com.ai.slp.route.cache.service;

import com.ai.slp.route.cache.dto.RouteGroup;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xin on 16-4-28.
 */
public interface IRouteGroupService {
    List<RouteGroup> queryAllRouteGroup(String tenantId) throws SQLException;

    RouteGroup queryRouteGroupById(String routeGroupId) throws SQLException;
}
