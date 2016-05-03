package com.ai.slp.route.cache.service.inter;

import com.ai.slp.route.cache.IRouteCache;
import com.ai.slp.route.cache.RouteCacheImpl;
import com.ai.slp.route.cache.dao.impl.RouteDaoImpl;
import com.ai.slp.route.cache.dao.impl.RouteGroupDaoImpl;
import com.ai.slp.route.cache.dao.impl.RouteRuleDaoImpl;
import com.ai.slp.route.cache.entity.Route;
import com.ai.slp.route.cache.service.impl.RouteGroupServiceImpl;
import com.ai.slp.route.common.config.RedisKeyConfig;
import com.ai.slp.route.common.entity.CycleUnit;
import com.ai.slp.route.common.entity.RuleBaseInfo;
import com.ai.slp.route.common.entity.RuleItem;
import com.ai.slp.route.common.entity.TimeType;
import com.ai.slp.route.common.util.RedisUtil;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

/**
 * Created by xin on 16-5-3.
 */
public class RouteCacheTest {

    private IRouteCache routeCacheA;

    @Test
    public void testRefreshAllCache() throws SQLException, ParseException {
        routeCacheA.refreshAllCache("SLP-001");
        String value = RedisUtil.hLoad(RedisKeyConfig.RK_Route("ROUTE-001"), "RT-RULE-001");
        RuleBaseInfo ruleBaseInfo = new Gson().fromJson(value, RuleBaseInfo.class);
        assertEquals(ruleBaseInfo.getMaxQuantity(), 100D, 0);
        assertEquals(ruleBaseInfo.getMinQuantity(), -1D, 0);
        assertEquals(ruleBaseInfo.getRuleItem(), RuleItem.ORDERCOUNT);
        assertEquals(ruleBaseInfo.getCycleUnit(), CycleUnit.DAY);
        assertEquals(ruleBaseInfo.getCycleValue(), 3);
        assertEquals(ruleBaseInfo.getTimeType(), TimeType.CYCLE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals(ruleBaseInfo.getValidateTime(), new Timestamp(simpleDateFormat.parse("2016-04-27 00:00:00").getTime()));
        assertEquals(ruleBaseInfo.getInvalidateTime(), new Timestamp(simpleDateFormat.parse("2016-05-05 23:59:59").getTime()));
    }

    @Before
    public void initialBean() {
        RouteGroupServiceImpl routeGroupService = new RouteGroupServiceImpl();
        routeGroupService.setRouteDao(new RouteDaoImpl());
        routeGroupService.setRouteGroupDao(new RouteGroupDaoImpl());
        routeGroupService.setRouteRuleDao(new RouteRuleDaoImpl());

        RouteCacheImpl routeCache = new RouteCacheImpl();
        routeCache.setiRouteGroupService(routeGroupService);
        routeCacheA = routeCache;
    }

    @Test
    public void testGson() {
        String value = new Gson().toJson(Route.RouteStatus.INVALIDATE);
        System.out.println(value);
        assertEquals(Route.RouteStatus.INVALIDATE, new Gson().fromJson(value, Route.RouteStatus.class));
    }
}
