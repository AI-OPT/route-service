package com.ai.slp.route.dubbo.impl;

import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.slp.route.api.core.interfaces.IRouteCoreService;
import com.ai.slp.route.api.core.params.SaleProductInfo;

/**
 * Created by xin on 16-5-10.
 */
public class TestSupplierRouteService {

    public static void main(String[] args) {
        SaleProductInfo saleProductInfo = new SaleProductInfo();
        saleProductInfo.setTenantId("SLP");
        saleProductInfo.setRouteGroupId("1000000001");
        saleProductInfo.setTotalConsumption(9980);
        System.out.println(DubboConsumerFactory.getService(IRouteCoreService.class)
                .findRoute(saleProductInfo));
    }
}
