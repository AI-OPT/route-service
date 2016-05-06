package com.ai.slp.route.service.server.interfaces;

import com.ai.slp.route.service.server.params.IRouteServerRequest;
import com.ai.slp.route.service.server.params.RouteServerResponse;

public interface IRouteServer {
    RouteServerResponse callServerByRouteId(IRouteServerRequest request);

    RouteServerResponse callServerByServerId(IRouteServerRequest request);
}
