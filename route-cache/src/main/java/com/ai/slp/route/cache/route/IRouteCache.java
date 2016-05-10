package com.ai.slp.route.cache.route;

import java.sql.SQLException;

public interface IRouteCache {
    boolean refreshAllCache(String tenantId) throws SQLException;

    boolean refreshRouteGroup(String routeGroupId) throws SQLException;

    boolean refreshRoute(String routeId);

    boolean refreshRule(String ruleId);
}
