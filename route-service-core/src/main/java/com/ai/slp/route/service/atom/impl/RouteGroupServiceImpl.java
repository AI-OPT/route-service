package com.ai.slp.route.service.atom.impl;

import com.ai.slp.route.cache.dao.IRouteDao;
import com.ai.slp.route.cache.dao.IRouteGroupDao;
import com.ai.slp.route.cache.dao.IRouteRuleDao;
import com.ai.slp.route.cache.dto.PriorityRoutesMapping;
import com.ai.slp.route.cache.dto.Route;
import com.ai.slp.route.cache.dto.RouteGroup;
import com.ai.slp.route.cache.dto.RouteRule;
import com.ai.slp.route.service.atom.interfaces.IRouteGroupService;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xin on 16-4-28.
 */
public class RouteGroupServiceImpl implements IRouteGroupService {

    private IRouteGroupDao routeGroupDao;

    private IRouteRuleDao routeRuleDao;

    private IRouteDao routeDao;

    @Override
    public List<RouteGroup> queryAllRouteGroup(String tenantId) throws SQLException {
        //先查询正常的RouteGroup
        List<RouteGroup> routeGroups = routeGroupDao.queryAllNormalRouteGroups(tenantId);
        // 查询优先级
        for (RouteGroup routeGroup : routeGroups) {
            List<PriorityRoutesMapping> routesMappings = routeGroupDao.queryPriorityRoutes(routeGroup.getRouteGroupId());
            filledPriorityRoutesMappingObject(routesMappings);
            routeGroup.addPriorityMapping(routesMappings);
        }

        return routeGroups;
    }

    @Override
    public RouteGroup queryRouteGroupById(String routeGroupId) throws SQLException {
        RouteGroup routeGroup = routeGroupDao.queryNormalRouteGroup(routeGroupId);
        List<PriorityRoutesMapping> routesMappings = routeGroupDao.queryPriorityRoutes(routeGroup.getRouteGroupId());
        filledPriorityRoutesMappingObject(routesMappings);
        routeGroup.addPriorityMapping(routesMappings);
        return routeGroup;
    }

    private void filledPriorityRoutesMappingObject(List<PriorityRoutesMapping> routesMappings) throws SQLException {
        for (PriorityRoutesMapping mapping : routesMappings) {
            for (Route route : mapping.getRouteList()) {
                if (!routeDao.checkStatusIsValidate(route.getRouteId())){
                    continue;
                }

                List<RouteRule> routeRules = routeRuleDao.queryRouteRuleByRouteId(route.getRouteId());
                route.setRouteRules(routeRules);
            }
        }
    }

    public void setRouteGroupDao(IRouteGroupDao routeGroupDao) {
        this.routeGroupDao = routeGroupDao;
    }

    public void setRouteRuleDao(IRouteRuleDao routeRuleDao) {
        this.routeRuleDao = routeRuleDao;
    }

    public void setRouteDao(IRouteDao routeDao) {
        this.routeDao = routeDao;
    }
}
