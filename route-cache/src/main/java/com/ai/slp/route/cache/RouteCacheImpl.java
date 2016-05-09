package com.ai.slp.route.cache;

import com.ai.slp.route.cache.entity.Route;
import com.ai.slp.route.cache.entity.RouteGroup;
import com.ai.slp.route.cache.entity.RouteRule;
import com.ai.slp.route.cache.service.inter.IRouteGroupService;
import com.ai.slp.route.cache.service.inter.IRouteRuleService;
import com.ai.slp.route.cache.service.inter.IRouteService;
import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.util.MCSUtil;
import com.ai.slp.route.common.util.RedisUtil;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class RouteCacheImpl implements IRouteCache {

    private Logger logger = LogManager.getLogger(RouteCacheImpl.class);

    private IRouteGroupService routeGroupService;

    private IRouteService routeService;

    private IRouteRuleService routeRuleService;

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
                MCSUtil.hput(RedisKeyConfig.RK_Route(routeRule.getRouteId()), routeRule.getRouteId(),
                        new Gson().toJson(routeRule.getRuleBaseInfo()));
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
