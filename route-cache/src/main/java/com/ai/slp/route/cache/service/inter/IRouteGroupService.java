package com.ai.slp.route.cache.service.inter;

import com.ai.slp.route.cache.entity.RouteGroup;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xin on 16-4-28.
 */
public interface IRouteGroupService {
    List<RouteGroup> queryAllRouteGroup(String tenantId) throws SQLException;

    RouteGroup queryRouteGroupById(String routeGroupId) throws SQLException;
}
