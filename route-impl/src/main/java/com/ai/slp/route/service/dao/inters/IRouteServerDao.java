package com.ai.slp.route.service.dao.inters;

import com.ai.slp.route.service.entity.RouteServer;

import java.sql.SQLException;

public interface IRouteServerDao {
    RouteServer queryRouteServerByServerId(String serverId) throws SQLException;
}
