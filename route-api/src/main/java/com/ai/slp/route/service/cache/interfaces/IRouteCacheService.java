package com.ai.slp.route.service.cache.interfaces;

import com.ai.opt.base.exception.SystemException;

/**
 * Created by xin on 16-4-22.
 */
public interface IRouteCacheService {

    boolean refreshAllCache(String tenantId) throws SystemException;

    boolean refreshRouteGroup(String routeGroup) throws SystemException;

    boolean refreshRoute(String routeId) throws SystemException;

    boolean refreshRule(String ruleId) throws SystemException;
}
