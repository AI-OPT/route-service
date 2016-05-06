package com.ai.slp.route.service.server.params;

/**
 * Created by xin on 16-5-5.
 */
public class RouteServerResponse {

    private String responseCode;

    private String responseMessage;

    private String responseData;

    public RouteServerResponse(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
