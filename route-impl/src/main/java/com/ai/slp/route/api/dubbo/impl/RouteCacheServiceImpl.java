package com.ai.slp.route.api.dubbo.impl;

import com.ai.opt.base.exception.SystemException;
import com.ai.slp.route.api.cache.interfaces.IRouteCacheService;
import com.ai.slp.route.api.cache.param.RouteCacheRequest;
import com.ai.slp.route.cache.route.IRouteCache;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class RouteCacheServiceImpl implements IRouteCacheService {

    private Logger logger = LogManager.getLogger(RouteCacheServiceImpl.class);

    @Autowired
    private IRouteCache routeCache;


    @Override
    public boolean refreshAllCache(RouteCacheRequest request) throws SystemException {
        try {
            return routeCache.refreshAllCache(request.getTenantId());
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("999999", "Failed to refresh all route group Cache");
        }
    }

    @Override
    public boolean refreshRouteGroup(RouteCacheRequest request) throws SystemException {
        try {
            return routeCache.refreshRouteGroup(request.getRouteGroupId());
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("999999", "Failed to refresh route group cache");
        }
    }

    @Override
    public boolean refreshRoute(RouteCacheRequest request) throws SystemException {
        try {
            return routeCache.refreshRoute(request.getRouteId());
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("999999", "Failed to refresh route cache");
        }
    }

    @Override
    public boolean refreshRule(RouteCacheRequest request) throws SystemException {
        try {
            return routeCache.refreshRule(request.getRuleId());
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("999999", "Failed to refresh route rule cache");
        }
    }
}
