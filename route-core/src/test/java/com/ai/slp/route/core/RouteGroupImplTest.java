package com.ai.slp.route.core;

import org.junit.Test;

/**
 * Created by xin on 16-5-3.
 */
public class RouteGroupImplTest {

    @Test
    public void testSwitchRoute() {
        for (int i = 0; i < 6; i++) {
            IRouteGroup iRouteGroup = new RouteGroupImpl();
            String routeId = iRouteGroup.switchRoute("SLP-001", "RT-GROUP-001", "{\"amount\":20, \"orderCount\":1}");
            System.out.println(routeId);
        }
    }
}
