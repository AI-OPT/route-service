package com.ai.slp.route.cache.service;

import com.ai.slp.route.cache.dao.impl.RouteDaoImpl;
import com.ai.slp.route.cache.dao.impl.RouteGroupDaoImpl;
import com.ai.slp.route.cache.dao.impl.RouteRuleDaoImpl;
import com.ai.slp.route.cache.dto.Route;
import com.ai.slp.route.service.atom.impl.RouteGroupServiceImpl;
import com.ai.slp.route.service.business.impl.RouteCacheImpl;
import com.ai.slp.route.service.business.interfaces.IRouteCache;
import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.ai.slp.route.vo.CycleUnit;
import com.ai.slp.route.vo.RuleBaseInfo;
import com.ai.slp.route.vo.RuleType;
import com.ai.slp.route.vo.TimeType;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

/**
 * Created by xin on 16-5-3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:context/core-context.xml")
public class RouteCacheTest {

    private IRouteCache routeCacheA;

    @Test
    public void testRefreshAllCache() throws SQLException, ParseException {
        routeCacheA.refreshAllCache("SLP-001");
        String value = MCSUtil.hLoad(CacheKeyUtil.RK_Route("ROUTE-001"), "RT-RULE-001");
        RuleBaseInfo ruleBaseInfo = new Gson().fromJson(value, RuleBaseInfo.class);
        assertEquals(ruleBaseInfo.getMaxQuantity(), 100D, 0);
        assertEquals(ruleBaseInfo.getMinQuantity(), -1D, 0);
        assertEquals(ruleBaseInfo.getRuleType(), RuleType.ORDERCOUNT);
        assertEquals(ruleBaseInfo.getCycleUnit(), CycleUnit.DAY);
        assertEquals(ruleBaseInfo.getCycleValue(), 3);
        assertEquals(ruleBaseInfo.getTimeType(), TimeType.CYCLE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals(ruleBaseInfo.getValidateTime(), new Timestamp(simpleDateFormat.parse("2016-04-27 00:00:00").getTime()));
        assertEquals(ruleBaseInfo.getInvalidateTime(), new Timestamp(simpleDateFormat.parse("2016-05-05 23:59:59").getTime()));
    }

    @Test
    public void refreshRouteGroupTest(){

        routeCacheA.refreshRoute("900000001");
    }

    @Before
    public void initialBean() {
        RouteGroupServiceImpl routeGroupService = new RouteGroupServiceImpl();
        routeGroupService.setRouteDao(new RouteDaoImpl());
        routeGroupService.setRouteGroupDao(new RouteGroupDaoImpl());
        routeGroupService.setRouteRuleDao(new RouteRuleDaoImpl());

        RouteCacheImpl routeCache = new RouteCacheImpl();
        routeCache.setRouteGroupService(routeGroupService);
        routeCacheA = routeCache;
    }

    @Test
    public void testGson() {
        String value = new Gson().toJson(Route.RouteStatus.INVALIDATE);
        System.out.println(value);
        assertEquals(Route.RouteStatus.INVALIDATE, new Gson().fromJson(value, Route.RouteStatus.class));
    }
}
