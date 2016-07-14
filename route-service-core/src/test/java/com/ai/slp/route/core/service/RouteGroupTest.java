package com.ai.slp.route.core.service;

import com.ai.slp.route.api.core.interfaces.IRouteCoreService;
import com.ai.slp.route.api.core.params.SaleProductInfo;
import com.ai.slp.route.service.business.impl.RouteSwitcherImpl;
import com.ai.slp.route.service.business.interfaces.IRouteSwitcher;
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
        routeCoreService.findRoute(productInfo);
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


}
