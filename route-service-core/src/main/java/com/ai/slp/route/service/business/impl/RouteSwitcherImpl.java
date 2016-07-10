package com.ai.slp.route.service.business.impl;

import com.ai.opt.sdk.util.CollectionUtil;
import com.ai.slp.route.service.business.interfaces.IRouteSwitcher;
import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.ai.slp.route.vo.Route;
import com.ai.slp.route.vo.RouteGroup;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RouteSwitcherImpl implements IRouteSwitcher {

    private Logger logger = LoggerFactory.getLogger(RouteSwitcherImpl.class);

    @Override
    public Route switchRoute(String tenantId, String groupId, String dataJson) {
        Route route = null;
        Map<String,List<String>> priorityRoutesMap = loadPriorityRoutesOfGroup(tenantId,groupId);
        if (!priorityRoutesMap.isEmpty()) {
            route = switchRoute(priorityRoutesMap, dataJson);
        }else
            logger.error("Failed to find the route group, tenantId:{}, groupId:{}", tenantId, groupId);

        return route;
    }

    /**
     * 从路由组中选择合适路由
     * @param priorityRoutesMap
     * @param dataJson
     * @return
     */
    private Route switchRoute(Map<String,List<String>> priorityRoutesMap, String dataJson) {
        List<String> priorities = new ArrayList<String>(priorityRoutesMap.keySet());
        Collections.sort(priorities);
        Route route = null;
        //获取路由组中优先级对应路由,逐级检查是否符合条件
        for (String priority : priorities) {
            List<String> routeIdList = priorityRoutesMap.get(priority);
            if (CollectionUtil.isEmpty(routeIdList))
                continue;
            route = switchRoute(priority,dataJson,routeIdList);
            if (route != null) {
                break;
            }
        }
        return route;
    }

    /**
     * 获取路由组下优先级对应路由信息
     * @param tenantId
     * @param routeGroupId
     * @return
     */
    private Map<String,List<String>> loadPriorityRoutesOfGroup(String tenantId, String routeGroupId){
        String routeGroupStatus = MCSUtil.load(CacheKeyUtil.RK_RouteGroupStatus(tenantId, routeGroupId));
        //若路由组状态为无效
        if (!RouteGroup.RouteGroupStatus.VALIDATE.getValue().equals(routeGroupStatus)) {
            logger.warn("tenantId:{}  routeGroupId:{}  status:{}.", tenantId, routeGroupId, routeGroupStatus);
            return null;
        }
        //查询路由组下优先级中路由组成
        Map<String, String> priorityRouteMap = MCSUtil.hLoads(CacheKeyUtil.RK_RouteGroup(tenantId, routeGroupId));
        Map<String,List<String>> priorityRouteIdMap = new HashMap<>();
        Set<String> prioritySet = priorityRouteMap.keySet();
        for (String priority : prioritySet) {
            String routeIdsVal = priorityRouteMap.get(priority);
            List<String> routeIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(routeIdsVal)){
                Collections.addAll(routeIdList,routeIdsVal.split(","));
            }
            priorityRouteIdMap.put(priority,routeIdList);
        }
        return priorityRouteIdMap;
    }

    /**
     * 从指定优先级中选择路由
     * @param priority
     * @param dataJson
     * @param routeInRedisKeyArray
     * @return
     */
    private Route switchRoute(String priority,String dataJson,List<String> routeInRedisKeyArray) {
        //产生一个随机数
        int index = ThreadLocalRandom.current().nextInt(0, routeInRedisKeyArray.size());
        int i = index;
        Route route = null;
        while (true) {
            String routeRedisIds = routeInRedisKeyArray.get(i);
            route = Route.load(routeRedisIds);
            //如果等于空
            if (route == null) {
                //检查下一个
                i = (++i) % routeInRedisKeyArray.size();
                if (i == index) {
                    logger.info("Can not found match route in PriorityId[{}], Will attempt to choose next priority",
                            priority);
                    break;
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("choose RoutId:{} to test Data", route.getRouteId());
            }

            if (!route.isOutOfRules(dataJson)) {
                break;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("RoutId:{} is out of rules", route.getRouteId());
            }
        }


        return route;
    }
}
