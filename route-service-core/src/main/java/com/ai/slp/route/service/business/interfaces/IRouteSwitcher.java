package com.ai.slp.route.service.business.interfaces;

import com.ai.slp.route.core.Route;

/**
 * Created by xin on 16-4-27.
 */
public interface IRouteSwitcher {
    Route switchRoute(String tenantId, String groupId, String dataJson);
}
