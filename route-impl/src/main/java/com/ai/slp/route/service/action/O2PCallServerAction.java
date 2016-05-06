package com.ai.slp.route.service.action;

import com.ai.slp.protocol.JsonProtocolConverter;
import com.ai.slp.route.service.entity.RouteServer;

/**
 * Created by xin on 16-5-6.
 */
public class O2PCallServerAction implements ICallServerAction {

    private String requestUrl;
    private String requestData;
    private String responseTemp;

    public O2PCallServerAction(RouteServer routeServer, String requestDate) {
        this.requestUrl = routeServer.getRequestURL();
        this.requestData = new JsonProtocolConverter().convert(routeServer.getRequestParam(), requestDate);
        this.responseTemp = routeServer.getResponseParam();
    }

    @Override
    public String doCall() {
        return null;
    }
}
