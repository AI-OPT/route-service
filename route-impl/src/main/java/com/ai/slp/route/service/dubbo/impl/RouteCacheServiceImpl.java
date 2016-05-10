package com.ai.slp.route.service.dubbo.impl;

import com.ai.opt.base.exception.SystemException;
import com.ai.slp.route.cache.route.IRouteCache;
import com.ai.slp.route.service.cache.interfaces.IRouteCacheService;
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
    public boolean refreshAllCache(String tenantId) throws SystemException {
        try {
            return routeCache.refreshAllCache(tenantId);
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("999999", "Failed to refresh all route group Cache");
        }
    }

    @Override
    public boolean refreshRouteGroup(String routeGroupId) throws SystemException {
        try {
            return routeCache.refreshRouteGroup(routeGroupId);
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("999999", "Failed to refresh route group cache");
        }
    }

    @Override
    public boolean refreshRoute(String routeId) throws SystemException {
        try {
            return routeCache.refreshRoute(routeId);
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("999999", "Failed to refresh route cache");
        }
    }

    @Override
    public boolean refreshRule(String ruleId) throws SystemException {
        try {
            return routeCache.refreshRule(ruleId);
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("999999", "Failed to refresh route rule cache");
        }
    }
}
