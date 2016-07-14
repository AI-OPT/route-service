package com.ai.slp.route.vo;

import org.junit.Test;

/**
 * Created by jackieliu on 16/7/14.
 */
public class RouteTest {

    @Test
    public void loadTest(){
        Route route = Route.load("100000002");
        System.out.printf("%s status is %s\r\n",route.getRouteId(),route.getRouteStatus().getValue());
    }
}
