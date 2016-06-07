package com.ai.slp.route.api.service.inters;

import com.ai.slp.route.api.server.params.IRouteServerRequest;
import com.ai.slp.route.api.server.params.RouteServerResponse;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-5.
 */
public interface IRouteServerManager {
    RouteServerResponse callServerByServerId(IRouteServerRequest request);

    RouteServerResponse callServerByRouteId(IRouteServerRequest request) throws SQLException;


}
