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
路由的服务接口和服务实现，目前提供三类Dubbo服务.
* 刷新缓存类(com.ai.slp.route.service.dubbo.impl.IRouteCacheService)
* 路由服务类(com.ai.slp.route.service.dubbo.impl.IRouteServer)
* 供应商路由类(com.ai.slp.route.service.dubbo.impl.ISupplierRouteService)

### IRouteCacheService
刷新缓存服务共提供4种方法，存储规则：
* 刷新全部(RefreshAll)
    刷新全部指的是刷新某个租户底下的所有路由组，路由和路由规则，入参只需要传递租户ID即可，具体实现如下：
    1. 根据租户ID查询出所有可用的路由组（即状态以2开头）
    2. 根据路由组查询出该路由组下的所有可用路由（状态也是2开头）
    3. 根据路由加载规则
    4. 完成加载
* 刷新单个路由组(refreshRouteGroup)
    刷新单个路由组指的是刷新某个路由组下的所有路由和路由规则，入参只需要传递路由组ID即可，具体实现如下：
    1. 根据路由组查询出该路由组下的所有可用路由（状态也是2开头）
    2. 根据路由加载规则
    3. 完成加载
* 刷新单个路由(refreshRoute)
    刷新单个路由指的是刷新某个路由信息和该路由底下的规则，入参只需要传递路由ID即可，具体实现如下：
    1. 根据路由ID查询出该路由信息和所有可用规则（路由状态1开头）
    2. 根据路由加载规则
    3. 完成加载
* 刷新单个路由规则(refreshRule)
    刷新路由规则指的是刷新规则信息以及当前规则状态，入参只需要传递路由ID即可，具体实现如下：
    1. 根据路由ID查询出该规则信息
    2. 修改规则的状态和路由当前信息
    3. 完成加载




## 存储规则
目前所有的路由组，路由和规则都存放在Redis中，存储规则如下：
路由组信息： 二级key存储
- 一级：路由组ID + 租户ID
- 二级  : 优先级
- Value    : 路由ID,路由ID
路由组状态：
- Key ： 路由组ID+租户ID
- Value： N/INVALIDATE（或者其他）

路由信息：二级key存储
- 一级    :路由组ID-DATA
- 二级    :路由规则ID
- Value   :规则信息（生失效时间，区间....)

路由状态：
- Key ： 路由ID-STATUS
- Value： N/RELOAD/INVALIDATE（或者其他）

路由规则状态：
- Key： 路由规则ID-STATUS
- Value： N/RELOAD/INVALIDATE

路由规则信息：
- Key : 规则ID-规则类型-DATA
- Value： 当前规则的量
