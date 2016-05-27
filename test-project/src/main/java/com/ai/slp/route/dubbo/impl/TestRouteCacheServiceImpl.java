package com.ai.slp.route.dubbo.impl;

import com.ai.slp.route.api.cache.interfaces.IRouteCacheService;
import com.ai.slp.route.dubbo.impl.util.BeanUtil;

import java.sql.SQLException;

/**
 * Created by xin on 16-5-10.
 */

public class TestRouteCacheServiceImpl {

    public static void main(String[] args) throws SQLException {

        //System.out.println(BeanUtil.getBean(IRouteCacheService.class).refreshAllCache("SLP-001"));
        System.out.println(BeanUtil.getBean(IRouteCacheService.class).refreshRoute("ROUTE-001"));
    }
}
