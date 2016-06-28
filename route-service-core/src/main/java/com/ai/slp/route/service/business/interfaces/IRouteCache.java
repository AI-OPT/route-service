package com.ai.slp.route.service.business.interfaces;

import java.sql.SQLException;

public interface IRouteCache {
    boolean refreshAllCache(String tenantId) throws SQLException;

    boolean refreshRouteGroup(String routeGroupId) throws SQLException;

    boolean refreshRoute(String routeId);

    boolean refreshRule(String ruleId);
}
