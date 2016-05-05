package com.ai.slp.route.service.supplier.impl;

import com.ai.slp.route.common.entity.RuleItem;
import com.ai.slp.route.core.IRouteSwitcher;
import com.ai.slp.route.core.Route;
import com.ai.slp.route.service.supplier.interfaces.ISupplierRouteService;
import com.ai.slp.route.service.supplier.params.SaleProductInfo;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SupplierRouteService implements ISupplierRouteService {

    @Autowired
    private IRouteSwitcher routeGroup;

    @Override
    public String findSupplier(SaleProductInfo saleProductInfo) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RuleItem.AMOUNT.getFieldName(), saleProductInfo.getTotalConsumption());
        jsonObject.addProperty(RuleItem.ORDERCOUNT.getFieldName(), 1);
        Route route = routeGroup.switchRoute(saleProductInfo.getTenantId(), saleProductInfo.getRouteTypeId(),
                jsonObject.toString());
        if (route != null) {
            return route.getRouteId();
        }
        return null;
    }
}
