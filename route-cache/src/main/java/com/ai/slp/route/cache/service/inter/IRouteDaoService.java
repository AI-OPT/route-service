package com.ai.slp.route.cache.service.inter;

import com.ai.slp.route.cache.entity.Route;

/**
 * Created by xin on 16-5-3.
 */
public interface IRouteDaoService {
    Route queryRouteById(String routeId);
}
