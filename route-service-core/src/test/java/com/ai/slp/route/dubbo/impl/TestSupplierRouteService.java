package com.ai.slp.route.dubbo.impl;

import com.ai.slp.route.api.core.interfaces.IRouteCoreService;
import com.ai.slp.route.api.core.params.SaleProductInfo;
import com.ai.slp.route.dubbo.impl.util.BeanUtil;

/**
 * Created by xin on 16-5-10.
 */
public class TestSupplierRouteService {

    public static void main(String[] args) {
        SaleProductInfo saleProductInfo = new SaleProductInfo();
        saleProductInfo.setTenantId("SLP");
        saleProductInfo.setRouteGroupId("1000000001");
        saleProductInfo.setTotalConsumption(9980);
        System.out.println(BeanUtil.getBean(IRouteCoreService.class).findRoute(saleProductInfo));
    }
}
