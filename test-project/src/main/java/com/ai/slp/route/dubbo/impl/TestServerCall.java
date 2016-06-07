package com.ai.slp.route.dubbo.impl;

import com.ai.slp.route.api.cache.param.RouteCacheRequest;
import com.ai.slp.route.api.server.interfaces.IRouteServer;
import com.ai.slp.route.api.server.params.IRouteServerRequest;
import com.ai.slp.route.dubbo.impl.util.BeanUtil;
import com.alibaba.fastjson.JSONObject;

import java.sql.SQLException;

/**
 * Created by xin on 16-6-7.
 */
public class TestServerCall {

    public static void main(String[] args) throws SQLException {
        IRouteServerRequest request = new IRouteServerRequest();
        request.setTenantId("SLP");
        request.setRouteId("100000001");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId","123");
        jsonObject.put("bizType","10010");
        jsonObject.put("proId","123");
        jsonObject.put("accountVal","123");
        jsonObject.put("buyNum","123");
        jsonObject.put("unitPrice","123");
        jsonObject.put("notifyUrl","123");
        request.setRequestData(jsonObject.toJSONString());
        System.out.println(BeanUtil.getBean(IRouteServer.class).callServerByRouteId(request));
    }
}
