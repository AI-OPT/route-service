package com.ai.slp.route.dubbo.impl;

import com.ai.slp.route.api.cache.interfaces.IRouteCacheService;
import com.ai.slp.route.api.cache.param.RouteCacheRequest;
import com.ai.slp.route.api.test.ITestDubboSV;
import com.ai.slp.route.dubbo.impl.util.BeanUtil;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-10.
 */

public class TestRouteCacheServiceImpl {

    public static void main(String[] args) throws SQLException {
        RouteCacheRequest request = new RouteCacheRequest();
        request.setTenantId("SLP");
        System.out.println(BeanUtil.getBean(IRouteCacheService.class).refreshAllCache(request));
       // System.out.println(BeanUtil.getBean(IRouteCacheService.class).refreshRoute("ROUTE-001"));
//        BeanUtil.getBean(ITestDubboSV.class).sayHello();
    }
}
