package com.ai.slp.route.api.cache.param;

import com.ai.opt.base.vo.BaseInfo;

/**
 * Created by xin on 16-6-3.
 */
public class RouteCacheRequest extends BaseInfo {
    private String routeId;

    private String routeGroupId;

    private String ruleId;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteGroupId() {
        return routeGroupId;
    }

    public void setRouteGroupId(String routeGroupId) {
        this.routeGroupId = routeGroupId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
}
