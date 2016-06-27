package com.ai.slp.route.core;

/**
 * Created by xin on 16-4-27.
 */
public interface IRouteSwitcher {
    Route switchRoute(String tenantId, String groupId, String dataJson);
}
