package com.ai.slp.route.service.cache.impl;

import com.ai.slp.route.cache.IRouteCache;
import com.ai.slp.route.service.cache.interfaces.IRouteCacheService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;


@Service
public class RouteCacheServiceImpl implements IRouteCacheService {

    @Autowired
    private IRouteCache routeCache;

    @Override
    public boolean refreshAllCache(String tenantId) throws SQLException {
        return routeCache.refreshAllCache(tenantId);
    }

    @Override
    public boolean refreshRouteGroup(String routeGroupId) throws SQLException {
        return routeCache.refreshRouteGroup(routeGroupId);
    }

    @Override
    public boolean refreshRoute(String routeId) {
        return routeCache.refreshRoute(routeId);
    }

    @Override
    public boolean refreshRule(String ruleId) {
        return routeCache.refreshRule(ruleId);
    }
}
