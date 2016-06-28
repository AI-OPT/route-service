package com.ai.slp.route.dubbo.impl;

import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.slp.route.api.cache.interfaces.IRouteCacheService;
import com.ai.slp.route.api.cache.param.RouteCacheRequest;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-10.
 */

public class TestRouteCacheServiceImpl {

    public static void main(String[] args) throws SQLException {
        RouteCacheRequest request = new RouteCacheRequest();
        request.setTenantId("SLP");
        System.out.println(DubboConsumerFactory.getService(IRouteCacheService.class)
                .refreshAllCache(request));
        // System.out.println(BeanUtil.getBean(IRouteCacheService.class).refreshRoute("ROUTE-001"));
//        BeanUtil.getBean(ITestDubboSV.class).sayHello();
    }
}
