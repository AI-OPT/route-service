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

## route-common
SLP核心路由的基础包，主要提供基础能力，比如JDBC连接，Redis连接以及常用的实体类对象
以下为常用的类：<br/>
* DBQueryTemplate：JDBC查询的模板类，采用闭包的模式，省去了每一段JDBC代码都需要获取Connection和关闭Connection
* DBUtils: 获取JDBC连接的类，初始化会读取jdbc.properties文件
* RedisUtil:Redis的工具类,目前支持集群和单机两种模式，主要在初始化会读取redis.properties配置

## route-api  && route-impl
TODO

## protocol-convert
TODO