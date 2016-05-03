package com.ai.slp.route.cache.service.impl;

import com.ai.slp.route.cache.dao.inter.IRouteDao;
import com.ai.slp.route.cache.entity.Route;
import com.ai.slp.route.cache.service.inter.IRouteDaoService;

/**
 * Created by xin on 16-5-3.
 */
public class RouteServiceImpl implements IRouteDaoService {

    private IRouteDao routeDao;

    @Override
    public Route queryRouteById(String routeId) {
        //TODO
        return null;
    }
}
