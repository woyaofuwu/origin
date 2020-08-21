package com.asiainfo.veris.crm.order.soa.group.minorec.quickorder;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class QuickOrderDataListBean {

    public static IDataset getQuickorderData(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("CUST_ID", param.getString("CUST_ID"));
        params.put("EC_SERIAL_NUMBER", param.getString("EC_SERIAL_NUMBER"));
        params.put("NODE_ID", param.getString("NODE_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, T.NODE_ID, T.ACCEPT_MONTH, T.CUST_ID, T.PRODUCT_ID, ");
        parser.addSQL(" T.EC_SERIAL_NUMBER, T.CODING_STR1, T.CODING_STR2, T.CODING_STR3, T.CODING_STR4, ");
        parser.addSQL(" T.CODING_STR5, T.CODING_STR6, T.CODING_STR7, T.CODING_STR8, T.CODING_STR9,  ");
        parser.addSQL(" T.CODING_STR10, T.UPDATE_TIME, T.RSRV_STR1, T.RSRV_STR2,  ");
        parser.addSQL(" RSRV_STR3, RSRV_STR4, RSRV_STR5  ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_DATA T  ");
        parser.addSQL(" WHERE T.IBSYSID=:IBSYSID ");
        parser.addSQL(" AND T.SUB_IBSYSID=:SUB_IBSYSID  ");
        parser.addSQL(" AND T.PRODUCT_ID=:PRODUCT_ID  ");
        parser.addSQL(" AND T.CUST_ID=:CUST_ID  ");
        parser.addSQL(" AND T.EC_SERIAL_NUMBER=:EC_SERIAL_NUMBER ");
        parser.addSQL(" AND T.NODE_ID=:NODE_ID  ");
        parser.addSQL(" ORDER  BY UPDATE_TIME  DESC ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     ** 获取最新SUB_IBSYSID的数据。
     * 
     * @param param
     *            根据IBSYSID查询
     * @return IDataset
     * @throws Exception
     * @Date 2019年10月24日
     * @author xieqj
     */
    public static IDataset getNewQuickorderData(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT E.SUB_IBSYSID, ");
        parser.addSQL("        E.IBSYSID, ");
        parser.addSQL("        E.NODE_ID, ");
        parser.addSQL("        E.ACCEPT_MONTH, ");
        parser.addSQL("        E.CUST_ID, ");
        parser.addSQL("        E.PRODUCT_ID, ");
        parser.addSQL("        E.EC_SERIAL_NUMBER, ");
        parser.addSQL("        E.CODING_STR1, ");
        parser.addSQL("        E.CODING_STR2, ");
        parser.addSQL("        E.CODING_STR3, ");
        parser.addSQL("        E.CODING_STR4, ");
        parser.addSQL("        E.CODING_STR5, ");
        parser.addSQL("        E.CODING_STR6, ");
        parser.addSQL("        E.CODING_STR7, ");
        parser.addSQL("        E.CODING_STR8, ");
        parser.addSQL("        E.CODING_STR9, ");
        parser.addSQL("        E.CODING_STR10, ");
        parser.addSQL("        E.UPDATE_TIME, ");
        parser.addSQL("        E.RSRV_STR1, ");
        parser.addSQL("        E.RSRV_STR2, ");
        parser.addSQL("        E.RSRV_STR3, ");
        parser.addSQL("        E.RSRV_STR4, ");
        parser.addSQL("        E.RSRV_STR5 ");
        parser.addSQL("   FROM (SELECT T.IBSYSID, ");
        parser.addSQL("                T.SUB_IBSYSID, ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
        parser.addSQL("           FROM TF_B_EOP_QUICKORDER_DATA T ");
        parser.addSQL("          WHERE T.IBSYSID =:IBSYSID ) R, ");
        parser.addSQL("        TF_B_EOP_QUICKORDER_DATA E ");
        parser.addSQL("  WHERE E.SUB_IBSYSID = R.SUB_IBSYSID ");
        parser.addSQL("    AND E.IBSYSID = R.IBSYSID ");
        parser.addSQL("    AND E.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL("    AND R.G <= 1 ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryAuditQuickData(IData param) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL("  SELECT E.IBSYSID,   ");
        parser.addSQL("  E.NODE_ID,          ");
        parser.addSQL("  E.CUST_ID,          ");
        parser.addSQL("  E.PRODUCT_ID,       ");
        parser.addSQL("  E.EC_SERIAL_NUMBER, ");
        parser.addSQL("  E.CODING_STR1  CODING_DATA1, ");
        parser.addSQL("  E.CODING_STR2  CODING_DATA2, ");
        parser.addSQL("  E.CODING_STR3  CODING_DATA3, ");
        parser.addSQL("  E.CODING_STR4  CODING_DATA4, ");
        parser.addSQL("  E.CODING_STR5  CODING_DATA5, ");
        parser.addSQL("  E.CODING_STR6  CODING_DATA6, ");
        parser.addSQL("  E.CODING_STR7  CODING_DATA7, ");
        parser.addSQL("  E.CODING_STR8  CODING_DATA8, ");
        parser.addSQL("  E.CODING_STR9  CODING_DATA9, ");
        parser.addSQL("  E.CODING_STR10 CODING_DATA10 ");
        parser.addSQL("   FROM (SELECT T.IBSYSID, ");
        parser.addSQL("                T.SUB_IBSYSID, ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
        parser.addSQL("           FROM TF_B_EOP_QUICKORDER_DATA T ");
        parser.addSQL("          WHERE T.IBSYSID =:IBSYSID ) R, ");
        parser.addSQL("        TF_B_EOP_QUICKORDER_DATA E ");
        parser.addSQL("  WHERE E.SUB_IBSYSID = R.SUB_IBSYSID ");
        parser.addSQL("    AND E.IBSYSID = R.IBSYSID ");
        parser.addSQL("    AND R.G <= 1 ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryEopAttrData(IData param) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL("  SELECT T.ATTR_CODE,  ");
        parser.addSQL("  T.ATTR_VALUE, T.SUB_IBSYSID ");
        parser.addSQL(" FROM TF_B_EOP_ATTR T,  ");
        parser.addSQL(" (SELECT D.SUB_IBSYSID,D.IBSYSID,  ");
        parser.addSQL("  ROW_NUMBER() OVER(PARTITION BY d.IBSYSID ORDER BY d.SUB_IBSYSID DESC) G  ");
        parser.addSQL(" FROM TF_B_EOP_ATTR D WHERE D.IBSYSID = :IBSYSID ) A ");
        parser.addSQL(" WHERE A.G<=1  ");
        parser.addSQL(" AND A.IBSYSID = T.IBSYSID  ");
        parser.addSQL(" AND A.SUB_IBSYSID =T.SUB_IBSYSID  ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryEopOtherData(IData param) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL("  SELECT T.ATTR_CODE,  ");
        parser.addSQL("  T.ATTR_VALUE, T.SUB_IBSYSID ");
        parser.addSQL(" FROM TF_B_EOP_OTHER T,  ");
        parser.addSQL(" (SELECT D.SUB_IBSYSID,D.IBSYSID,  ");
        parser.addSQL("  ROW_NUMBER() OVER(PARTITION BY d.IBSYSID ORDER BY d.SUB_IBSYSID DESC) G  ");
        parser.addSQL(" FROM TF_B_EOP_OTHER D WHERE D.IBSYSID = :IBSYSID ) A ");
        parser.addSQL(" WHERE A.G<=1  ");
        parser.addSQL(" AND A.IBSYSID = T.IBSYSID  ");
        parser.addSQL(" AND A.SUB_IBSYSID =T.SUB_IBSYSID  ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryCustIdByIbsysid(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL("  SELECT T.CUST_ID   ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_DATA T  ");
        parser.addSQL(" WHERE T.IBSYSID=:IBSYSID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static int updateEopProductInfo(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("TRADE_ID", param.getString("TRADE_ID"));
        params.put("USER_ID", param.getString("USER_ID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        params.put("BUSIFORM_ID", param.getString("BUSIFORM_ID"));
        params.put("RECORD_NUM", param.getString("RECORD_NUM"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" UPDATE TF_B_EOP_PRODUCT T ");
        parser.addSQL(" SET T.TRADE_ID =:TRADE_ID, T.USER_ID =:USER_ID ,T.SERIAL_NUMBER =:SERIAL_NUMBER ,");
        parser.addSQL(" T.RSRV_STR2 =:BUSIFORM_ID ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.RECORD_NUM =:RECORD_NUM ");
        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static int updateUserInfo(IData param) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", param.getString("USER_ID"));
        params.put("CONTRACT_ID", param.getString("CONTRACT_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" UPDATE TF_F_USER T ");
        parser.addSQL(" SET T.CONTRACT_ID =:CONTRACT_ID ");
        parser.addSQL(" WHERE T.USER_ID =:USER_ID ");
        int results = Dao.executeUpdate(parser, Route.CONN_CRM_CG);
        String[] paramName = { "V_SYNC_OBJ_ID", "V_SYNC_TABLE_NAME", "V_RESULT_CODE", "V_RESULT_INFO" };
        IData paramValue = new DataMap();
        paramValue.put("V_SYNC_OBJ_ID", param.getString("USER_ID"));
        paramValue.put("V_SYNC_TABLE_NAME", "TF_F_USER");
        paramValue.put("V_RESULT_CODE", "");
        paramValue.put("V_RESULT_INFO", "");
        Dao.callProc("PK_CS_MANUAL_CRMTOBILL.SINGEL_MAIN", paramName, paramValue, Route.CONN_CRM_CG);
        if (!"0".equals(paramValue.getString("V_RESULT_CODE", "0"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, paramValue.getString("V_RESULT_INFO", ""));
        }
        return results;
    }

    public static IDataset qryUserInfoUserIdBySerialNumber(IData param) throws Exception {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL("  SELECT T.USER_ID   ");
        parser.addSQL(" FROM TF_F_USER T  ");
        parser.addSQL(" WHERE T.SERIAL_NUMBER=:SERIAL_NUMBER ");
        parser.addSQL(" AND T.REMOVE_TAG='0' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    public static IDataset qryAllQuickOrderInfoByIbsysidAndProductId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT E.* FROM TF_B_EOP_QUICKORDER_DATA E ");
        parser.addSQL(" WHERE E.SUB_IBSYSID = (SELECT MAX(T.SUB_IBSYSID) ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_DATA T ");
        parser.addSQL(" WHERE T.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.IBSYSID =:IBSYSID) ");
        parser.addSQL(" AND E.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT E.* FROM TF_Bh_EOP_QUICKORDER_DATA E ");
        parser.addSQL(" WHERE E.SUB_IBSYSID = (SELECT MAX(T.SUB_IBSYSID) ");
        parser.addSQL(" FROM TF_Bh_EOP_QUICKORDER_DATA T ");
        parser.addSQL(" WHERE T.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.IBSYSID =:IBSYSID) ");
        parser.addSQL(" AND E.PRODUCT_ID =:PRODUCT_ID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

}
