package com.ai.slp.route.api.service.impl;

import com.ai.slp.route.api.action.ICallServerAction;
import com.ai.slp.route.api.dao.inters.IRouteServerDao;
import com.ai.slp.route.api.entity.RouteServer;
import com.ai.slp.route.api.server.params.IRouteServerRequest;
import com.ai.slp.route.api.server.params.RouteServerResponse;
import com.ai.slp.route.api.service.inters.IRouteServerManager;
import com.ai.slp.route.cache.route.dao.IRouteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class RouteServerManagerImpl implements IRouteServerManager {


    @Autowired
    private IRouteServerDao routeServerDao;

    @Autowired
    private IRouteDao routeDao;


    @Override
    public RouteServerResponse callServerByServerId(IRouteServerRequest request) {
        RouteServerResponse routeServerResponse = new RouteServerResponse("000000");
        try {
            RouteServer routeServer = routeServerDao.queryRouteServerByServerId(request.getServerId());
            if (routeServer == null) {
                throw new RuntimeException("Cannot find the routeServer by serverID[" + request.getServerId() + "]");
            }

            ICallServerAction action = routeServer.getServerType().chooseCallServerAction(routeServer, request.getTenantId(), request.getRequestData());
            routeServerResponse.setResponseData(action.doCall());
        } catch (Exception e) {
            routeServerResponse.setResponseCode("999999");
        }

        return routeServerResponse;
    }

    @Override
    public RouteServerResponse callServerByRouteId(IRouteServerRequest request) throws SQLException {
        String serviceId = routeDao.queryServiceIdByRouteId(request.getRouteId());
        if (serviceId == null || serviceId.length() == 0) {
            RouteServerResponse response = new RouteServerResponse("999999");
            response.setResponseMessage("Can not find the serviceId in RouteID[" + request.getRouteId() + "]");
            return response;
        }
        request.setServerId(serviceId);
        return callServerByServerId(request);
    }

}
