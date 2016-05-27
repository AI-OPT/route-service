package com.ai.slp.route.api.server.params;

import com.ai.opt.base.vo.BaseInfo;

/**
 * Created by xin on 16-5-5.
 */
public class IRouteServerRequest extends BaseInfo {

    private String routeId;

    private String serverId;

    private String requestDate;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
}
