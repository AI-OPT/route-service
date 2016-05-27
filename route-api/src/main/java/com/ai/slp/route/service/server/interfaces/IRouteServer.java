package com.ai.slp.route.service.server.interfaces;

import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.exception.SystemException;
import com.ai.slp.route.service.server.params.IRouteServerRequest;
import com.ai.slp.route.service.server.params.RouteServerResponse;

/**
 * 路由服务的Dubbo服务<br>
 * <p>
 * Date: 2016年4月18日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 *
 * @author zhangxin10
 */
public interface IRouteServer {
    /**
     * 通过路由ID调用路由服务. <br>
     *
     * @param request 路由服务请求参数
     * @return 路由服务返回结果
     * @throws BusinessException
     * @throws SystemException
     * @author zhangxin10
     * @ApiDocMethod
     * @ApiCode ROUT_CORE_SERVICE_0001
     */
    RouteServerResponse callServerByRouteId(IRouteServerRequest request);

    /**
     * 通过服务ID调用路由服务. <br>
     *
     * @param request 路由服务请求参数
     * @return 路由服务返回结果
     * @throws BusinessException
     * @throws SystemException
     * @author zhangxin10
     * @ApiDocMethod
     * @ApiCode ROUT_CORE_SERVICE_0002
     */
    RouteServerResponse callServerByServerId(IRouteServerRequest request);
}
