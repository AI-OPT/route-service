package com.ai.slp.route.dao;

import com.ai.slp.route.vo.PriorityRoutesMapping;
import com.ai.slp.route.vo.RouteGroup;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xin on 16-4-28.
 */
public interface IRouteGroupDao {
    List<RouteGroup> queryAllNormalRouteGroups(String tenantId) throws SQLException;

    List<PriorityRoutesMapping> queryPriorityRoutes(String routeGroupId) throws SQLException;

    RouteGroup queryNormalRouteGroup(String routeGroupId) throws SQLException;
}
