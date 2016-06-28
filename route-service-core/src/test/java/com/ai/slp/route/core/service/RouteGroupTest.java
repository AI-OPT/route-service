package com.ai.slp.route.core.service;

import com.ai.slp.route.core.IRouteSwitcher;
import com.ai.slp.route.core.Route;
import com.ai.slp.route.core.RouteSwitcherImpl;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xin on 16-5-3.
 */
public class RouteGroupTest {

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
            Route route = iRouteSwitcher.switchRoute("SLP-001", "RT-GROUP-001", "{\"amount\":20, \"orderCount\":1}");
            System.out.println(Thread.currentThread().getName() + ":" + route.getRouteId());
            countDownLatch.countDown();
        }
    }


}
