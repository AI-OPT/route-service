package com.ai.slp.route.api.dao.inters;

import com.ai.slp.route.api.entity.RouteServer;

import java.sql.SQLException;

public interface IRouteServerDao {
    RouteServer queryRouteServerByServerId(String serverId) throws SQLException;
}
