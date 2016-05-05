package com.ai.slp.route.service.supplier.params;

/**
 * Created by xin on 16-4-22.
 */
public class SaleProductInfo {

    private String tenantId;

    private String routeTypeId;

    // 单位：厘
    private float totalConsumption;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRouteTypeId() {
        return routeTypeId;
    }

    public void setRouteTypeId(String routeTypeId) {
        this.routeTypeId = routeTypeId;
    }

    public float getTotalConsumption() {
        return totalConsumption;
    }

    public void setTotalConsumption(float totalConsumption) {
        this.totalConsumption = totalConsumption;
    }
}
