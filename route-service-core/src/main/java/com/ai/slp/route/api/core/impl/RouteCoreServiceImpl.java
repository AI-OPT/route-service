package com.ai.slp.route.api.core.impl;

import com.ai.opt.base.exception.SystemException;
import com.ai.opt.sdk.util.StringUtil;
import com.ai.slp.route.api.core.interfaces.IRouteCoreService;
import com.ai.slp.route.api.core.params.SaleProductInfo;
import com.ai.slp.route.service.business.interfaces.IRouteSwitcher;
import com.ai.slp.route.vo.Route;
import com.ai.slp.route.vo.RuleItem;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Service(validation = "true")
@Component
public class RouteCoreServiceImpl implements IRouteCoreService {

    private Logger logger = LogManager.getLogger(RouteCoreServiceImpl.class);

    @Autowired
    private IRouteSwitcher routeGroup;

    @Override
    public String findRoute(SaleProductInfo saleProductInfo) throws SystemException {
        try {
            String tenantId = saleProductInfo.getTenantId(),routeGroupId = saleProductInfo.getRouteGroupId();
            if (StringUtil.isBlank(tenantId) || StringUtil.isBlank(routeGroupId)){
                logger.error("Tenant id or Route group Id is null,tenantID:{},routeGroupId:{}",tenantId,routeGroupId);
                return null;
            }

            JsonObject jsonObject = new JsonObject();
            //金额
            jsonObject.addProperty(RuleItem.AMOUNT.getFieldName(), saleProductInfo.getTotalConsumption());
            //订单量
            jsonObject.addProperty(RuleItem.ORDERCOUNT.getFieldName(), 1);
            Route route = routeGroup.switchRoute(tenantId, routeGroupId,jsonObject.toString());

            return route == null?null:route.getRouteId();
        } catch (Exception e) {
            logger.error(e);
            throw new SystemException("Failed to find Supplier.", e);
        }
    }
}
