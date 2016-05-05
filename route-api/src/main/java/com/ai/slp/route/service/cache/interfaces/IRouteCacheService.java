package com.ai.slp.route.service.cache.interfaces;

import java.sql.SQLException;

/**
 * Created by xin on 16-4-22.
 */
public interface IRouteCacheService {
    boolean refreshAllCache(String tenantId) throws SQLException;

    boolean refreshRouteGroup(String routeGroup) throws SQLException;


    boolean refreshRoute(String routeId);

    boolean refreshRule(String ruleId);
}
