package com.ai.slp.route.service.dao.impl;

import com.ai.slp.route.common.util.DBQueryTemplate;
import com.ai.slp.route.service.dao.inters.IRouteServerDao;
import com.ai.slp.route.service.entity.RouteServer;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class RouteServerDao implements IRouteServerDao {

    @Override
    public RouteServer queryRouteServerByServerId(final String serverId) throws SQLException {
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<RouteServer>() {
            @Override
            public RouteServer query(Connection connection) throws SQLException {
                String sql = "SELECT A.SERV_ID,A.URL, A.REQUEST_PARAM, A.RETURN_PARAM, A.SERV_NAME, A.SERV_TYPE " +
                        "FROM SERV_INFO A WHERE A.SERV_ID = ? AND A.STATE LIKE ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, serverId);
                preparedStatement.setString(2, "2%");
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    RouteServer routeServer = new RouteServer(resultSet.getString("SERV_ID"), resultSet.getString("SERV_NAME"));
                    routeServer.setRequestURL(resultSet.getString("URL"));
                    routeServer.setRequestParam(resultSet.getString("REQUEST_PARAM"));
                    routeServer.setResponseParam(resultSet.getString("RETURN_PARAM"));
                    routeServer.setServerType(resultSet.getString("SERV_TYPE"));
                    return routeServer;
                }

                return null;
            }
        });
    }

    @Override
    public RouteServer queryRouteServerByRouteId(String routeId) {
        return null;
    }
}
