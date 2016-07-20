# route-service
SLP核心路由代码

# 工程简介

## route-api
路由的服务接口和服务实现，目前提供两类Dubbo服务.
* 刷新缓存类(com.ai.slp.route.service.dubbo.impl.IRouteCacheService)
* 路由服务类(com.ai.slp.route.service.dubbo.impl.IRouteServer)

调用代码如下：
```
// ruleData需要匹配规则的JSON串
IRouteSwitcher routeSwitcher = new RouteSwitcherImpl();
routeSwitcher(tenantId, routeGroupId, ruleData);

IRouteCache routeCache = new RouteCacheImpl();
routeCache.refreshAllCache(tenantId);
routeCache.refreshRouteGroup(routeGroupId);
routeCache.refreshRoute(routeId);
routeCache.refreshRule(ruleId);
```

## route-core
整个路由服务的核心，对route-api中接口的实现


## 存储规则
目前所有的路由组，路由和规则都存放在Redis中，存储规则如下：

路由组信息： 租户ID-路由组ID  ---- Hash
-   KEY ：优先级，如：1
- Value ：路由ID字符串，如：路由ID,路由ID

路由组状态： 租户ID-路由组ID -STATUS  ---- String
- Value ： Value： 路由组状态，如：2（正常），21（预警）

路由信息：路由ID ---- Hash
-   KEY ：路由规则标识
- Value ：规则信息的JSON串

路由状态：路由ID-STATUS ---- String
- Value ： 路由状态，如：2（正常），21（预警） 

路由规则量信息：路由规则ID-规则项-DATA ---- String
- Value ： 当前规则的量 ，如订单量、金额量

路由规则状态：路由规则ID-STATUS ---- String
- Value ： 路由规则状态，如:1(有效)，0(无效)，-1（待生效，未到生效时间），-2（待重新加载）

