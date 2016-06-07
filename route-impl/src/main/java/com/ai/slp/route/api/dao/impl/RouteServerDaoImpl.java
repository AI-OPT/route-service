package com.ai.slp.route.api.dao.impl;

import com.ai.slp.route.common.util.DBQueryTemplate;
import com.ai.slp.route.api.dao.inters.IRouteServerDao;
import com.ai.slp.route.api.entity.RouteServer;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RouteServerDaoImpl implements IRouteServerDao {
    @Override
    public RouteServer queryRouteServerByServerId(final String serverId) throws SQLException {
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<RouteServer>() {
            @Override
            public RouteServer query(Connection connection) throws SQLException {
                String sql = "SELECT SERV_ID, SERV_NAME , URL, REQUEST_PARAM,RETURN_PARAM,SERV_TYPE FROM route_serv_info WHERE  SERV_ID = ?  AND STATE LIKE ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, serverId);
                ps.setString(2, "2%");
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    RouteServer routeServer = new RouteServer(resultSet.getString("SERV_ID"), resultSet.getString("SERV_NAME"));
                    routeServer.setRequestParam(resultSet.getString("REQUEST_PARAM"));
                    routeServer.setRequestURL(resultSet.getString("URL"));
                    routeServer.setResponseParam(resultSet.getString("RETURN_PARAM"));
                    routeServer.setServerType(resultSet.getString("SERV_TYPE"));

                    return routeServer;
                }
                return null;
            }
        });
    }

}
