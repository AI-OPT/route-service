package com.ai.slp.route.cache;

import com.ai.slp.route.cache.entity.Route;
import com.ai.slp.route.cache.entity.RouteGroup;
import com.ai.slp.route.cache.service.inter.IRouteDaoService;
import com.ai.slp.route.cache.service.inter.IRouteGroupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class RouteCacheImpl implements IRouteCache {

    private Logger logger = LogManager.getLogger(RouteCacheImpl.class);

    private IRouteGroupService iRouteGroupService;

    private IRouteDaoService iRouteDaoService;

    @Override
    public boolean refreshAllCache(String tenantId) throws SQLException {
        try {
            List<RouteGroup> routeGroupList = iRouteGroupService.queryAllRouteGroup(tenantId);
            for (RouteGroup routeGroup : routeGroupList) {
                routeGroup.refreshCache();
            }
            return true;
        } catch (Exception e) {
            logger.error(e);
            logger.error("Cannot refresh all cache", e);
            return false;
        }
    }

    @Override
    public boolean refreshRouteGroup(String routeGroupId) throws SQLException {
        try {
            RouteGroup routeGroup = iRouteGroupService.queryRouteGroupById(routeGroupId);
            if (routeGroup != null) {
                routeGroup.refreshCache();
            }
            return true;
        } catch (Exception e) {
            logger.error(e);
            logger.error("Cannot refresh all cache", e);
            return false;
        }
    }

    @Override
    public boolean refreshRoute(String routeId) {
        try {
            Route route = iRouteDaoService.queryRouteById(routeId);
            if (route != null) {
                route.refreshCache();
            }
            return true;
        } catch (Exception e) {
            logger.error(e);
            logger.error("Cannot refresh all cache", e);
            return false;
        }
    }

    @Override
    public boolean refreshRule(String ruleId) {
        return false;
    }

    public void setiRouteGroupService(IRouteGroupService iRouteGroupService) {
        this.iRouteGroupService = iRouteGroupService;
    }
}
