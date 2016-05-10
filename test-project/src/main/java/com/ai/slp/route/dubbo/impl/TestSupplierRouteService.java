package com.ai.slp.route.dubbo.impl;

import com.ai.slp.route.dubbo.impl.util.BeanUtil;
import com.ai.slp.route.service.supplier.interfaces.ISupplierRouteService;
import com.ai.slp.route.service.supplier.params.SaleProductInfo;

/**
 * Created by xin on 16-5-10.
 */
public class TestSupplierRouteService {

    public static void main(String[] args) {
        SaleProductInfo saleProductInfo = new SaleProductInfo();
        saleProductInfo.setTenantId("SLP-001");
        saleProductInfo.setRouteGroupId("RT-GROUP-001");
        saleProductInfo.setTotalConsumption(20.5F);
        System.out.println(BeanUtil.getBean(ISupplierRouteService.class).findSupplier(saleProductInfo));
    }
}
