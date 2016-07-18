package com.ai.slp.route.util;

import com.ai.slp.route.vo.RuleItem;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

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

    @Test
    public void RK_RouteDataTest(){
        String cacheKey = CacheKeyUtil.RK_Route("500000003");
        Map<String,String> cache = MCSUtil.hLoads(cacheKey);
        Iterator<String> mapKey = cache.keySet().iterator();
        while (mapKey.hasNext()){
            String key = mapKey.next();
            System.out.println("The key:"+key);
            System.out.println("The value:"+cache.get(key));
        }
    }
}
