
package com.asiainfo.veris.crm.order.soa.frame.bcf.dao;

import java.sql.Connection;

import com.ailk.database.config.DBRouteCfg;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.service.session.SessionManager;

public final class ConnMgr
{
    public static DBConnection getConnection(String routeId) throws Exception
    {
        String connDb = getDbByRouteIdForCrm(routeId);
        return SessionManager.getInstance().getSessionConnection(connDb);
    }

    public static String getDbByRouteId(String routeGrp, String routeId) throws Exception
    {

        return DBRouteCfg.getRoute(routeGrp, routeId);
    }

    public static String getDbByRouteIdForCrm(String eparchyCode) throws Exception
    {

        return getDbByRouteId("crm", eparchyCode);
    }

    public static Connection getDBConn() throws Exception
    {
        return null;
    }

    public static Connection getDBConn(String routeId) throws Exception
    {

        return getConnection(routeId).getConnection();
    }
}
