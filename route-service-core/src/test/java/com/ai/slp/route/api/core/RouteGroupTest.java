package com.ai.slp.route.api.core;

import com.ai.slp.route.api.core.interfaces.IRouteCoreService;
import com.ai.slp.route.api.core.params.SaleProductInfo;
import com.ai.slp.route.service.business.impl.RouteSwitcherImpl;
import com.ai.slp.route.service.business.interfaces.IRouteSwitcher;
import com.ai.slp.route.util.CacheKeyUtil;
import com.ai.slp.route.util.MCSUtil;
import com.ai.slp.route.vo.RuleItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xin on 16-5-3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:context/core-context.xml")
public class RouteGroupTest {

    @Autowired
    IRouteCoreService routeCoreService;

    @Test
    public void findRouteTest(){
        SaleProductInfo productInfo = new SaleProductInfo();
        productInfo.setTenantId("SLP");
        productInfo.setRouteGroupId("9987654321");
        productInfo.setTotalConsumption(10f);
        String routeId = routeCoreService.findRoute(productInfo);
        System.out.println(routeId);
        printRoleCacheVal();
    }

    @Test
    public void testSwitchRoute() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 0; i < 6; i++) {
            new TestThread(countDownLatch).start();
        }

        countDownLatch.await();
    }


    public class TestThread extends Thread {

        private CountDownLatch countDownLatch;

        public TestThread(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            IRouteSwitcher iRouteSwitcher = new RouteSwitcherImpl();
//            Route route = iRouteSwitcher.switchRoute("SLP-001", "RT-GROUP-001", "{\"amount\":20, \"orderCount\":1}");
//            System.out.println(Thread.currentThread().getName() + ":" + route.getRouteId());
            countDownLatch.countDown();
        }
    }

    @Test
    public void printRoleCacheVal(){
        String ruleId = "9000002";
        printRuleState(ruleId);
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData(ruleId, RuleItem.ORDERCOUNT));
        printRuleState("9000003");
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData("9000003", RuleItem.AMOUNT));
        printRuleState("6000001");
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData("6000001", RuleItem.AMOUNT));
        printRuleState("9000008");
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData("9000008", RuleItem.ORDERCOUNT));
        printRuleState("9000009");
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData("9000009", RuleItem.AMOUNT));
        printRuleState("9000010");
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData("9000010", RuleItem.AMOUNT));
        printRuleState("9000012");
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData("9000012", RuleItem.ORDERCOUNT));
        printRuleState("9000011");
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData("9000011", RuleItem.ORDERCOUNT));
        ruleId = "5000004";
        printRuleState(ruleId);
        printRuleDataCache(CacheKeyUtil.RK_RouteRuleData(ruleId, RuleItem.AMOUNT));
    }

    @Test
    public void printRouteCacheVal(){
        String routeId = "";
        printRouteState(routeId);
    }

    /**
     * 打印路由规则量
     * @param cacheKey
     */
    private void printRuleDataCache(String cacheKey){
        System.out.printf("RK [%S] values is %s \r\n",cacheKey, MCSUtil.load(cacheKey));
    }

    /**
     * 打印路由规则状态
     * @param ruleId
     */
    private void printRuleState(String ruleId){
        String stateKey = CacheKeyUtil.RK_RouteRuleStatus(ruleId);
        System.out.printf("RK [%S] status is %s \r\n",stateKey,MCSUtil.load(stateKey));
    }

    /**
     * 打印路由状态
     * @param routeId
     */
    private void printRouteState(String routeId){
        String stateKey = CacheKeyUtil.RK_RouteStatus(routeId);
        System.out.printf("RK [%S] status is %s \r\n",stateKey,MCSUtil.load(stateKey));
    }

}
