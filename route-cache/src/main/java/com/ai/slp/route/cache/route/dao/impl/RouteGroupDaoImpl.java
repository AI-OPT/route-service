package com.ai.slp.route.cache.route.dao.impl;

import com.ai.slp.route.cache.route.dao.IRouteGroupDao;
import com.ai.slp.route.cache.route.dto.PriorityRoutesMapping;
import com.ai.slp.route.cache.route.dto.Route;
import com.ai.slp.route.cache.route.dto.RouteGroup;
import com.ai.slp.route.common.util.DBQueryTemplate;
import com.ai.slp.route.common.util.DBUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class RouteGroupDaoImpl implements IRouteGroupDao {

    private Logger logger = LogManager.getLogger(RouteGroupDaoImpl.class);

    private DBUtils dbUtils;

    @Override
    public List<RouteGroup> queryAllNormalRouteGroups(final String tenantId) throws SQLException {
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<List<RouteGroup>>() {
            @Override
            public List<RouteGroup> query(Connection connection) throws SQLException {
                List<RouteGroup> routeGroups = new ArrayList<RouteGroup>();
                String sql = "SELECT  TENANT_ID, ROUTE_GROUP_ID, ROUTE_GROUP_NAME,STATE FROM ROUTE_GROUP  WHERE STATE LIKE ? AND TENANT_ID = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, "2%");
                ps.setString(2, tenantId);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    RouteGroup routeGroup = new RouteGroup(resultSet.getString("TENANT_ID"), resultSet.getString("ROUTE_GROUP_ID"), resultSet.getString("ROUTE_GROUP_NAME"));
                    routeGroup.setGroupStatus(RouteGroup.RouteGroupStatus.convert(resultSet.getString("STATE")));
                    routeGroups.add(routeGroup);
                }
                return routeGroups;
            }
        });
    }

    @Override
    public List<PriorityRoutesMapping> queryPriorityRoutes(final String routeGroupId) throws SQLException {
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<List<PriorityRoutesMapping>>() {
            @Override
            public List<PriorityRoutesMapping> query(Connection connection) throws SQLException {
                List<PriorityRoutesMapping> routesMappings = new ArrayList<PriorityRoutesMapping>();
                String sql = "SELECT ROUTE.ROUTE_ID, ROUTE_ITEM_ID, ROUTE_GROUP_ID, PRIORITY_NUMBER, SERIAL_NUMBER, ROUTE.STATE AS STATE FROM ROUTE_ITEM,ROUTE WHERE ROUTE_GROUP_ID = ? AND ROUTE_ITEM.STATE = ? AND  ROUTE.STATE like ? AND ROUTE.ROUTE_ID = ROUTE_ITEM.ROUTE_ID ORDER BY PRIORITY_NUMBER, SERIAL_NUMBER";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, routeGroupId);
                ps.setString(2, "1");
                ps.setString(3, "2%");
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    PriorityRoutesMapping mapping = new PriorityRoutesMapping(resultSet.getString("ROUTE_ITEM_ID"), resultSet.getString("SERIAL_NUMBER"));
                    int index = routesMappings.indexOf(mapping);

                    if (index != -1) {
                        mapping = routesMappings.get(index);
                    } else {
                        routesMappings.add(mapping);
                    }

                    Route route = new Route(resultSet.getString("ROUTE_ID"), resultSet.getString("STATE"));
                    mapping.addRoute(route);
                }
                return routesMappings;
            }
        });
    }

    @Override
    public RouteGroup queryNormalRouteGroup(final String routeGroupId) throws SQLException {
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<RouteGroup>() {
            @Override
            public RouteGroup query(Connection connection) throws SQLException {
                RouteGroup routeGroups = null;
                String sql = "SELECT  TENANT_ID, ROUTE_GROUP_ID, ROUTE_GROUP_NAME,STATE FROM ROUTE_GROUP  WHERE STATE LIKE ? AND ROUTE_GROUP_ID = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, "2%");
                ps.setString(2, routeGroupId);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    routeGroups = new RouteGroup(resultSet.getString("TENANT_ID"), resultSet.getString("ROUTE_GROUP_ID"), resultSet.getString("ROUTE_GROUP_NAME"));
                    routeGroups.setGroupStatus(RouteGroup.RouteGroupStatus.convert(resultSet.getString("STATE")));
                }
                return routeGroups;
            }
        });
    }
}
