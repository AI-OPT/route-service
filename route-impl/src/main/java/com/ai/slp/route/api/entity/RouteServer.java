package com.ai.slp.route.api.entity;

import com.ai.slp.route.api.action.ICallServerAction;
import com.ai.slp.route.api.action.O2PCallServerAction;
import com.ai.slp.route.api.action.RedirectCallServerAction;

/**
 * Created by xin on 16-5-5.
 */
public class RouteServer {
    private String serverId;
    private String serverName;
    private ServerType serverType;
    private String requestURL;
    private String requestParam;
    private String responseParam;

    public RouteServer(String serverId, String serverName) {
        this.serverId = serverId;
        this.serverName = serverName;
    }

    public void setServerType(String serverType) {
        this.serverType = ServerType.convert(serverType);
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public void setResponseParam(String responseParam) {
        this.responseParam = responseParam;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public enum ServerType {
        HTTP, REDIRECT;

        public static ServerType convert(String value) {
            switch (value) {
                case "H":
                    return HTTP;
                case "R":
                    return REDIRECT;
                default:
                    throw new RuntimeException("Can not conver the serverType value[" + value + "]");
            }
        }


        public ICallServerAction chooseCallServerAction(RouteServer routeServer, String tenantId, String requestDate) {
            switch (this) {
                case HTTP: {
                    return new O2PCallServerAction(routeServer, tenantId, requestDate);
                }
                case REDIRECT:
                    return new RedirectCallServerAction();
                default:
                    throw new RuntimeException("Can not conver the serverType value[" + this + "]");
            }
        }
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public String getResponseParam() {
        return responseParam;
    }
}
