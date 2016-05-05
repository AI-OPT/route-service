# route-service
SLP核心路由代码

# 工程简介
## route-core
整个路由服务的核心，主要通过租户ID和RouteGroupId查找符合规则路由ID
调用代码

```
// ruleData需要匹配规则的JSON串
IRouteSwitcher routeSwitcher = new RouteSwitcherImpl();
routeSwitcher(tenantId, routeGroupId, ruleData);
```

## route-cache
完成将路由信息和路由规则加载到缓存中。目前主要提供四个方法：
* refreshAllCache：刷新某个租户的所有的路由组，路由和路由规则的信息
* refreshRouteGroup：刷新某个路由组下的所有的路由和路由规则
* refreshRoute：刷新某个路由的路由规则的信息
* refreshRule：刷新某个路由规则的信息

调用代码如下：
```
IRouteCache routeCache = new RouteCacheImpl();
routeCache.refreshAllCache(tenantId);
routeCache.refreshRouteGroup(routeGroupId);
routeCache.refreshRoute(routeId);
routeCache.refreshRule(ruleId);
```
