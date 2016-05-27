package com.ai.slp.route.api.action;

import com.ai.slp.route.api.entity.RouteServer;
import com.ai.slp.route.api.util.HttpUtil;
import com.ai.slp.route.api.util.ProtocolConvert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by xin on 16-5-6.
 */
public class O2PCallServerAction implements ICallServerAction {

    private Logger logger = LogManager.getLogger(O2PCallServerAction.class);

    private String requestUrl;
    private String requestTemplate;
    private String responseTemplate;
    private String requestData;

    public O2PCallServerAction(RouteServer routeServer, String requestDate) {
        this.requestUrl = routeServer.getRequestURL();
        this.requestTemplate = routeServer.getRequestParam();
        this.responseTemplate = routeServer.getResponseParam();
        this.requestData = requestDate;
    }

    @Override
    public String doCall() throws IOException {
        String requestValue = ProtocolConvert.convert(requestTemplate, requestData);
        logger.info("Request Template : {} ", requestTemplate);
        logger.info("Request Data : {} ", requestData);
        logger.info("Request Value : {} ", requestValue);
        String responseData = HttpUtil.doPostRequest(requestUrl, "", requestValue);
        String responseValue = ProtocolConvert.convert(responseTemplate, responseData);
        logger.info("Request Template : {} ", responseTemplate);
        logger.info("Request Data : {} ", responseData);
        logger.info("Request Value : {} ", responseValue);
        return responseData;
    }
}
