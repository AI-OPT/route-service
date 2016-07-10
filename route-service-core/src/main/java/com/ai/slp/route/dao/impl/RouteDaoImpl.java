package com.ai.slp.route.dao.impl;

import com.ai.slp.route.dao.IRouteDao;
import com.ai.slp.route.util.DBQueryTemplate;
import com.ai.slp.route.vo.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xin on 16-4-29.
 */
@Deprecated
public class RouteDaoImpl implements IRouteDao {
    @Override
    public boolean checkStatusIsValidate(final String routeId) throws SQLException {
        final String sql = "SELECT COUNT(*) AS totalSize FROM ROUTE WHERE ROUTE_ID=? AND STATE LIKE ?";
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<Boolean>() {
            @Override
            public Boolean query(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, routeId);
                ps.setString(2, "2%");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("totalSize") > 0 ? true : false;
                }
                return false;
            }
        });
    }

    @Override
    public Route queryRouteById(final String routeId) throws SQLException {
        final String sql = "SELECT ROUTE_ID, STATE FROM ROUTE WHERE ROUTE_ID=? AND STATE LIKE ?";
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<Route>() {
            @Override
            public Route query(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, routeId);
                ps.setString(2, "2%");
                ResultSet rs = ps.executeQuery();
                Route route = null;
                if (rs.next()) {
                    route = new Route(rs.getString("ROUTE_ID"), rs.getString("STATE"));
                }

                return route;
            }
        });
    }

    @Override
    public String queryServiceIdByRouteId(final String routeId) throws SQLException {
        final String sql = "SELECT SERV_ID FROM ROUTE WHERE ROUTE_ID=? AND STATE LIKE ?";
        return DBQueryTemplate.query(new DBQueryTemplate.Executor<String>() {
            @Override
            public String query(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, routeId);
                ps.setString(2, "2%");
                ResultSet rs = ps.executeQuery();
                Route route = null;
                if (rs.next()) {
                    return rs.getString("SERV_ID");
                }

                return null;
            }
        });
    }
}
