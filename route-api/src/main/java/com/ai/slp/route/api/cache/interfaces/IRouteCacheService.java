package com.ai.slp.route.api.cache.interfaces;

import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.exception.SystemException;
import com.ai.slp.route.api.cache.param.RouteCacheRequest;

/**
 * 路由缓存服务<br>
 * <p>
 * Date: 2016年4月18日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 *
 * @author zhangxin10
 */
public interface IRouteCacheService {

    /**
     * 根据租户ID刷新路由缓存. <br>
     *
     * @param request 租户ID
     * @return 是否刷新成功
     * @throws BusinessException
     * @throws SystemException
     * @author zhangxin10
     * @ApiDocMethod
     * @ApiCode ROUTE_CORE_CACHE_0001
     */
    boolean refreshAllCache(RouteCacheRequest request) throws SystemException;

    /**
     * 根据路由组ID刷新路由缓存. <br>
     *
     * @param request 路由组ID
     * @return 是否刷新成功
     * @throws BusinessException
     * @throws SystemException
     * @author zhangxin10
     * @ApiDocMethod
     * @ApiCode ROUTE_CORE_CACHE_0002
     */
    boolean refreshRouteGroup(RouteCacheRequest request) throws SystemException;

    /**
     * 根据路由ID刷新路由缓存. <br>
     *
     * @param request 路由ID
     * @return 是否刷新成功
     * @throws BusinessException
     * @throws SystemException
     * @author zhangxin10
     * @ApiDocMethod
     * @ApiCode ROUTE_CORE_CACHE_0003
     */
    boolean refreshRoute(RouteCacheRequest request) throws SystemException;

    /**
     * 根据路由ID刷新路由缓存. <br>
     *
     * @param request 路由ID
     * @return 是否刷新成功
     * @throws BusinessException
     * @throws SystemException
     * @author zhangxin10
     * @ApiDocMethod
     * @ApiCode ROUTE_CORE_CACHE_0004
     */
    boolean refreshRule(RouteCacheRequest request) throws SystemException;
}
