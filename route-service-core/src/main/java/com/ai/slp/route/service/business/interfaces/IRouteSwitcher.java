package com.ai.slp.route.service.business.interfaces;


import com.ai.slp.route.vo.Route;

/**
 * Created by xin on 16-4-27.
 */
public interface IRouteSwitcher {
    /**
     * 进行路由选择
     * @param tenantId
     * @param groupId
     * @param dataJson
     * @return
     */
    Route switchRoute(String tenantId, String groupId, String dataJson);
}
