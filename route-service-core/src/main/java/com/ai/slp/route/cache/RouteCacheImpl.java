package com.ai.slp.route.cache;

import com.ai.slp.route.cache.dto.Route;
import com.ai.slp.route.cache.dto.RouteGroup;
import com.ai.slp.route.cache.dto.RouteRule;
import com.ai.slp.route.cache.service.IRouteGroupService;
import com.ai.slp.route.cache.service.IRouteRuleService;
import com.ai.slp.route.cache.service.IRouteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class RouteCacheImpl implements IRouteCache {

    private Logger logger = LogManager.getLogger(RouteCacheImpl.class);

    private IRouteGroupService routeGroupService;

    private IRouteService routeService;

    private IRouteRuleService routeRuleService;

    /**
     * 刷新指定租户下所有路由规则
     * @param tenantId
     * @return
     * @throws SQLException
     */
    @Override
    public boolean refreshAllCache(String tenantId) throws SQLException {
        try {
            List<RouteGroup> routeGroupList = routeGroupService.queryAllRouteGroup(tenantId);
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

    /**
     * 刷新指定路由组下路由规则
     * @param routeGroupId
     * @return
     * @throws SQLException
     */
    @Override
    public boolean refreshRouteGroup(String routeGroupId) throws SQLException {
        try {
            RouteGroup routeGroup = routeGroupService.queryRouteGroupById(routeGroupId);
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
            Route route = routeService.queryRouteById(routeId);
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
        try {
            RouteRule routeRule = routeRuleService.queryRouteRuleById(ruleId);
            if (routeRule != null) {
                //需要更新Route的信息
                Route route = routeService.queryRouteById(routeRule.getRouteId());
                if (route != null) {
                    route.refreshRouteData();
                }
                routeRule.refreshCache();
            }
            return true;
        } catch (Exception e) {
            logger.error(e);
            logger.error("Cannot refresh all cache", e);
            return false;
        }

    }

    public void setRouteGroupService(IRouteGroupService routeGroupService) {
        this.routeGroupService = routeGroupService;
    }

    public void setRouteService(IRouteService routeService) {
        this.routeService = routeService;
    }

    public void setRouteRuleService(IRouteRuleService routeRuleService) {
        this.routeRuleService = routeRuleService;
    }
}
