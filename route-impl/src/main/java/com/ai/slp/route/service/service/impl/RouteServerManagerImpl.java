package com.ai.slp.route.service.service.impl;

import com.ai.slp.route.cache.route.dao.IRouteDao;
import com.ai.slp.route.service.action.ICallServerAction;
import com.ai.slp.route.service.dao.inters.IRouteServerDao;
import com.ai.slp.route.service.entity.RouteServer;
import com.ai.slp.route.service.server.params.RouteServerResponse;
import com.ai.slp.route.service.service.inters.IRouteServerManager;
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
    public RouteServerResponse callServerByServerId(String serverId, String requestDate) {
        RouteServerResponse routeServerResponse = new RouteServerResponse("000000");
        try {
            RouteServer routeServer = routeServerDao.queryRouteServerByServerId(serverId);
            if (routeServer == null) {
                throw new RuntimeException("Cannot find the routeServer by serverID[" + serverId + "]");
            }

            ICallServerAction action = routeServer.getServerType().chooseCallServerAction(routeServer, requestDate);
            routeServerResponse.setResponseData(action.doCall());
        } catch (Exception e) {
            routeServerResponse.setResponseCode("999999");
        }

        return routeServerResponse;
    }

    @Override
    public RouteServerResponse callServerByRouteId(String routeId, String requestDate) throws SQLException {
        String serviceId = routeDao.queryServiceIdByRouteId(routeId);
        if (serviceId != null) {
            RouteServerResponse response = new RouteServerResponse("999999");
            response.setResponseMessage("Can not find the serviceId in RouteID[" + routeId + "]");
            return response;
        }

        return callServerByServerId(routeId, requestDate);
    }

}
