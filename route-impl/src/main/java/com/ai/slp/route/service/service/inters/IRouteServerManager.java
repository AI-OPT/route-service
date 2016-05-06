package com.ai.slp.route.service.service.inters;

import com.ai.slp.route.service.server.params.RouteServerResponse;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-5.
 */
public interface IRouteServerManager {
    RouteServerResponse callServerByServerId(String serverId, String requestDate);

    RouteServerResponse callServerByRouteId(String routeId, String requestDate) throws SQLException;
}
