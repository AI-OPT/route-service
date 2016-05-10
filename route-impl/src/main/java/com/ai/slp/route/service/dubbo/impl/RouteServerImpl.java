package com.ai.slp.route.service.dubbo.impl;

import com.ai.opt.base.exception.SystemException;
import com.ai.slp.route.service.server.interfaces.IRouteServer;
import com.ai.slp.route.service.server.params.IRouteServerRequest;
import com.ai.slp.route.service.server.params.RouteServerResponse;
import com.ai.slp.route.service.service.inters.IRouteServerManager;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class RouteServerImpl implements IRouteServer {

    private Logger logger = LogManager.getLogger(RouteServerImpl.class);

    @Autowired
    private IRouteServerManager routeServerManager;

    @Override
    public RouteServerResponse callServerByRouteId(IRouteServerRequest request){

        if (request.getTenantId() == null || request.getTenantId().length() == 0) {
            throw new RuntimeException("TenantId can not be null");
        }

        if (request.getRouteId() == null || request.getRouteId().length() == 0) {
            throw new RuntimeException("routeID can not be null");
        }

        try {

            return routeServerManager.callServerByRouteId(request.getRouteId(), request.getRequestDate());
        } catch (Exception e) {
            logger.error("Failed to call server by route Id", e);
        }

        return new RouteServerResponse("999999");
    }

    @Override
    public RouteServerResponse callServerByServerId(IRouteServerRequest request) {
        if (request.getTenantId() == null || request.getTenantId().length() == 0) {
            throw new RuntimeException("TenantId can not be null");
        }

        if (request.getServerId() == null || request.getServerId().length() == 0) {
            throw new RuntimeException("routeID can not be null");
        }

        return routeServerManager.callServerByServerId(request.getServerId(), request.getRequestDate());
    }
}
