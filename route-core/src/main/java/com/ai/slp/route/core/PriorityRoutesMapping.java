package com.ai.slp.route.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 优先级与路由映射
 * Created by xin on 16-4-27.
 */
public class PriorityRoutesMapping {
    private Logger logger = LogManager.getLogger(PriorityRoutesMapping.class);
    private String priority; //优先级
    private String[] routeInRedisKeyArray;//优先级下所有路由的cache的key

    public PriorityRoutesMapping(String priority, String routeIds) {
        this.priority = priority;
        this.routeInRedisKeyArray = routeIds.split(",");
    }

    public Route switchRoute(String dataJson) {
        int index = ThreadLocalRandom.current().nextInt(0, routeInRedisKeyArray.length);
        int i = index;
        Route route = null;
        boolean resultFlag = false;
        while (true) {
            String routeRedisIds = routeInRedisKeyArray[i];
            route = Route.load(routeRedisIds);
            if (route != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("choose RoutId:{} to test Data", route.getRouteId());
                }

                if (!route.isOutOfRules(dataJson)) {
                    resultFlag = true;
                    return route;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("RoutId:{} is out of rules", route.getRouteId());
                }
            }

            i = (++i) % routeInRedisKeyArray.length;
            if (i == index) {
                logger.info("Can not found match route in PriorityId[{}], Will attempt to choose next priority",
                        priority);
                break;
            }
        }

        if (resultFlag) {
            return route;
        } else {
            return null;
        }
    }
}
