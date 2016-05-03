package com.ai.slp.route.cache.dao.inter;

import java.sql.SQLException;

/**
 * Created by xin on 16-4-29.
 */
public interface IRouteDao {
    boolean checkStatusIsValidate(String routeId) throws SQLException;
}
