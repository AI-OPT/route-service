package com.ai.slp.route.util;

import com.ai.slp.route.vo.RuleItem;
import org.junit.Test;

/**
 * Created by jackieliu on 16/7/15.
 */
public class CacheKeyUtilTest {

    @Test
    public void RK_RouteRuleDataTest(){
        printCache(CacheKeyUtil.RK_RouteRuleData("8000001", RuleItem.AMOUNT));
        printCache(CacheKeyUtil.RK_RouteRuleData("8000002", RuleItem.ORDERCOUNT));
        printCache(CacheKeyUtil.RK_RouteRuleData("8000003", RuleItem.AMOUNT));
        printCache(CacheKeyUtil.RK_RouteRuleData("8000004", RuleItem.ORDERCOUNT));
        printCache(CacheKeyUtil.RK_RouteRuleData("8000005", RuleItem.AMOUNT));
        printCache(CacheKeyUtil.RK_RouteRuleData("8000006", RuleItem.ORDERCOUNT));
    }

    private void printCache(String cacheKey){
        System.out.printf("RK [%S] values is %s \r\n",cacheKey,MCSUtil.load(cacheKey));
    }
}
