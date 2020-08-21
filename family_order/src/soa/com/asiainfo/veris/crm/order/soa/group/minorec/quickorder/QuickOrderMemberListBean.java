package com.asiainfo.veris.crm.order.soa.group.minorec.quickorder;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class QuickOrderMemberListBean {

    public static IDataset getQuickOrderMember(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("CUST_ID", param.getString("CUST_ID"));
        params.put("EC_SERIAL_NUMBER", param.getString("EC_SERIAL_NUMBER"));
        params.put("NODE_ID", param.getString("NODE_ID"));

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.SUB_IBSYSID,  T.IBSYSID,  T.NODE_ID,  T.ACCEPT_MONTH,  T.CUST_ID,  T.PRODUCT_ID ");
        parser.addSQL(" , T.SERIAL_NUMBER,  T.EC_SERIAL_NUMBER,  T.CODING_STR1,  T.CODING_STR2 ");
        parser.addSQL(" , T.CODING_STR3,  T.CODING_STR4,  T.CODING_STR5,  T.CODING_STR6,  T.CODING_STR7  ");
        parser.addSQL(" , T.CODING_STR8,  T.CODING_STR9,  T.CODING_STR10,  T.CODING_STR11,  T.CODING_STR12 ");
        parser.addSQL(" , T.CODING_STR13,  T.CODING_STR14,  T.CODING_STR15,  T.CODING_STR16,  T.CODING_STR17 ");
        parser.addSQL(" , T.CODING_STR18,  T.CODING_STR19,  T.CODING_STR20,  T.UPDATE_TIME ");
        parser.addSQL(" , T.RSRV_STR1,  T.RSRV_STR2,  T.RSRV_STR3,  T.RSRV_STR4,  T.RSRV_STR5 ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_MEB T  ");
        parser.addSQL(" WHERE T.IBSYSID=:IBSYSID ");
        parser.addSQL(" AND T.SUB_IBSYSID=:SUB_IBSYSID  ");
        parser.addSQL(" AND T.PRODUCT_ID=:PRODUCT_ID  ");
        parser.addSQL(" AND T.CUST_ID=:CUST_ID  ");
        parser.addSQL(" AND T.EC_SERIAL_NUMBER=:EC_SERIAL_NUMBER ");
        parser.addSQL(" AND T.NODE_ID=:NODE_ID  ");
        parser.addSQL(" ORDER  BY T.UPDATE_TIME  DESC ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset getMemberInfoListBySysId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
        params.put("NODE_ID", param.getString("NODE_ID"));

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T1.IBSYSID, T1.CUST_ID,  T1.PRODUCT_ID AS OFFER_CODE, T2.PRODUCT_ID,");
        parser.addSQL(" T1.EC_SERIAL_NUMBER, T2.CODING_STR1, T2.CODING_STR2");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_DATA T1 LEFT JOIN TF_B_EOP_QUICKORDER_MEB T2  ");
        parser.addSQL(" ON T1.IBSYSID = T2.IBSYSID AND T1.EC_SERIAL_NUMBER = T2.EC_SERIAL_NUMBER ");
        parser.addSQL(" WHERE T1.IBSYSID=:IBSYSID ");
        parser.addSQL(" AND T2.SUB_IBSYSID=:SUB_IBSYSID  ");
        parser.addSQL(" AND T2.NODE_ID=:NODE_ID  ");
        parser.addSQL(" ORDER  BY T2.UPDATE_TIME  DESC ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset getNewsMemberInfoListBySysId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.SUB_IBSYSID,  T.IBSYSID,  T.NODE_ID,  T.ACCEPT_MONTH,  T.CUST_ID,  T.PRODUCT_ID ");
        parser.addSQL(" , T.SERIAL_NUMBER,  T.EC_SERIAL_NUMBER,  T.CODING_STR1,  T.CODING_STR2 ");
        parser.addSQL(" , T.CODING_STR3,  T.CODING_STR4,  T.CODING_STR5,  T.CODING_STR6,  T.CODING_STR7  ");
        parser.addSQL(" , T.CODING_STR8,  T.CODING_STR9,  T.CODING_STR10,  T.CODING_STR11,  T.CODING_STR12");
        parser.addSQL(" , T.CODING_STR13,  T.CODING_STR14,  T.CODING_STR15,  T.CODING_STR16,  T.CODING_STR17 ");
        parser.addSQL(" , T.CODING_STR18,  T.CODING_STR19,  T.CODING_STR20,  T.UPDATE_TIME ");
        parser.addSQL(" , T.RSRV_STR1,  T.RSRV_STR2,  T.RSRV_STR3,  T.RSRV_STR4,  T.RSRV_STR5 ");
        parser.addSQL(" FROM (SELECT MAX(R.SUB_IBSYSID) AS SUB_IBSYSID FROM TF_B_EOP_QUICKORDER_MEB R WHERE R.IBSYSID =:IBSYSID) A ");
        parser.addSQL(" , TF_B_EOP_QUICKORDER_MEB T WHERE T.SUB_IBSYSID = A.SUB_IBSYSID ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     ** 根据IBSYSID和PRODUCT_ID 查询最新的esp成员信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @Date 2019年10月26日
     * @author xieqj
     */
    public static IDataset getEspNewsMemberInfoList(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.SUB_IBSYSID,  ");
        parser.addSQL("        T.IBSYSID, ");
        parser.addSQL("        T.NODE_ID, ");
        parser.addSQL("        T.ACCEPT_MONTH, ");
        parser.addSQL("        T.CUST_ID, ");
        parser.addSQL("        T.PRODUCT_ID, ");
        parser.addSQL("        T.SERIAL_NUMBER, ");
        parser.addSQL("        T.EC_SERIAL_NUMBER, ");
        parser.addSQL("        T.CODING_STR1, ");
        parser.addSQL("        T.CODING_STR2, ");
        parser.addSQL("        T.CODING_STR3, ");
        parser.addSQL("        T.CODING_STR4, ");
        parser.addSQL("        T.CODING_STR5, ");
        parser.addSQL("        T.CODING_STR6, ");
        parser.addSQL("        T.CODING_STR7, ");
        parser.addSQL("        T.CODING_STR8, ");
        parser.addSQL("        T.CODING_STR9, ");
        parser.addSQL("        T.CODING_STR10, ");
        parser.addSQL("        T.CODING_STR11, ");
        parser.addSQL("        T.CODING_STR12, ");
        parser.addSQL("        T.CODING_STR13, ");
        parser.addSQL("        T.CODING_STR14, ");
        parser.addSQL("        T.CODING_STR15, ");
        parser.addSQL("        T.CODING_STR16, ");
        parser.addSQL("        T.CODING_STR17, ");
        parser.addSQL("        T.CODING_STR18, ");
        parser.addSQL("        T.CODING_STR19, ");
        parser.addSQL("        T.CODING_STR20, ");
        parser.addSQL("        T.UPDATE_TIME, ");
        parser.addSQL("        T.RSRV_STR1, ");
        parser.addSQL("        T.RSRV_STR2, ");
        parser.addSQL("        T.RSRV_STR3, ");
        parser.addSQL("        T.RSRV_STR4, ");
        parser.addSQL("        T.RSRV_STR5 ");
        parser.addSQL("   FROM (SELECT MAX(R.SUB_IBSYSID) AS SUB_IBSYSID ");
        parser.addSQL("           FROM TF_B_EOP_QUICKORDER_MEB R ");
        parser.addSQL("          WHERE R.IBSYSID = :IBSYSID ) A, ");
        parser.addSQL("        TF_B_EOP_QUICKORDER_MEB T ");
        parser.addSQL("  WHERE T.SUB_IBSYSID = A.SUB_IBSYSID ");
        parser.addSQL("    AND T.PRODUCT_ID = :PRODUCT_ID ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryAuditQuickMember(IData param) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("EC_SERIAL_NUMBER", param.getString("EC_SERIAL_NUMBER"));
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT E.SERIAL_NUMBER, ");
        parser.addSQL("        E.SUB_IBSYSID,   ");
        parser.addSQL("        E.IBSYSID,       ");
        parser.addSQL("        E.NODE_ID,       ");
        parser.addSQL("        E.ACCEPT_MONTH,  ");
        parser.addSQL("        E.CUST_ID,       ");
        parser.addSQL("        E.PRODUCT_ID,    ");
        parser.addSQL("        E.UPDATE_TIME,   ");
        parser.addSQL("        E.PRODUCT_ID,    ");
        parser.addSQL("        E.CODING_STR1,   ");
        parser.addSQL("        E.CODING_STR2,   ");
        parser.addSQL("        E.CODING_STR3,   ");
        parser.addSQL("        E.CODING_STR4,   ");
        parser.addSQL("        E.CODING_STR5,   ");
        parser.addSQL("        E.CODING_STR6,   ");
        parser.addSQL("        E.CODING_STR7,   ");
        parser.addSQL("        E.CODING_STR8,   ");
        parser.addSQL("        E.CODING_STR9,   ");
        parser.addSQL("        E.CODING_STR10,  ");
        parser.addSQL("        E.CODING_STR11,  ");
        parser.addSQL("        E.CODING_STR12,  ");
        parser.addSQL("        E.CODING_STR13,  ");
        parser.addSQL("        E.CODING_STR14,  ");
        parser.addSQL("        E.CODING_STR15,  ");
        parser.addSQL("        E.CODING_STR16,  ");
        parser.addSQL("        E.CODING_STR17,  ");
        parser.addSQL("        E.CODING_STR18,  ");
        parser.addSQL("        E.CODING_STR19,  ");
        parser.addSQL("        E.CODING_STR20,  ");
        parser.addSQL("        E.RSRV_STR1,  ");
        parser.addSQL("        E.RSRV_STR2,  ");
        parser.addSQL("        E.RSRV_STR3,  ");
        parser.addSQL("        E.RSRV_STR4,  ");
        parser.addSQL("        E.RSRV_STR5,  ");
        parser.addSQL("        E.EC_SERIAL_NUMBER ");
        parser.addSQL(" FROM (SELECT T.IBSYSID,         ");
        parser.addSQL("              T.SUB_IBSYSID,     ");
        parser.addSQL("              T.EC_SERIAL_NUMBER, ");
        parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_MEB T  ");
        parser.addSQL(" WHERE T.IBSYSID = :IBSYSID AND T.EC_SERIAL_NUMBER = :EC_SERIAL_NUMBER ) R, ");
        parser.addSQL(" TF_B_EOP_QUICKORDER_MEB E       ");
        parser.addSQL(" WHERE E.SUB_IBSYSID = R.SUB_IBSYSID ");
        parser.addSQL("       AND E.IBSYSID = R.IBSYSID ");
        parser.addSQL("       AND R.EC_SERIAL_NUMBER = E.EC_SERIAL_NUMBER ");
        parser.addSQL("       AND R.G <= 1 ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryArchivesInfo(IData param) throws Exception {

        IData params = new DataMap();
        params.put("CUST_ID", param.getString("CUST_ID"));

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT P.PRODUCT_ID EC_PRODUCT_ID,    ");
        parser.addSQL("        U.SERIAL_NUMBER, ");
        parser.addSQL("        U.CONTRACT_ID,   ");
        parser.addSQL("        E.ARCHIVES_ID,   ");
        parser.addSQL("        A.ARCHIVES_NAME, ");
        parser.addSQL("        E.PRODUCT_ID,    ");
        parser.addSQL("        P.USER_ID        ");
        parser.addSQL(" from TF_F_ELECTRONIC_AGREEMENT E, ");
        parser.addSQL("      TF_F_ELECTRONIC_ARCHIVES  A, ");
        parser.addSQL("      TF_F_USER                 U, ");
        parser.addSQL("      TF_F_USER_PRODUCT         P  ");
        parser.addSQL(" WHERE U.CUST_ID =:CUST_ID         ");
        parser.addSQL("       AND U.USER_ID=P.USER_ID     ");
        parser.addSQL("       AND U.CONTRACT_ID=E.AGREEMENT_ID ");
        parser.addSQL("       AND E.ARCHIVES_ID=A.ARCHIVES_ID  ");
        parser.addSQL("       AND SYSDATE < A.END_DATE         ");
        parser.addSQL("       AND U.REMOVE_TAG='0'             ");
        parser.addSQL("       ORDER BY  U.CONTRACT_ID DESC     ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);

    }

    public static IDataset qryMemInfoForCustManager(IData param) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT E.SERIAL_NUMBER,E.SUB_IBSYSID,E.IBSYSID,E.NODE_ID,E.ACCEPT_MONTH,E.CUST_ID,E.PRODUCT_ID, ");
        parser.addSQL(" E.UPDATE_TIME,E.PRODUCT_ID,E.CODING_STR1,E.CODING_STR2,E.CODING_STR3,E.CODING_STR4, ");
        parser.addSQL(" E.CODING_STR5,E.CODING_STR6,E.CODING_STR7,E.CODING_STR8,E.CODING_STR9, ");
        parser.addSQL(" E.CODING_STR10,E.CODING_STR11,E.CODING_STR12,E.CODING_STR13,E.CODING_STR14, ");
        parser.addSQL(" E.CODING_STR15,E.CODING_STR16,E.CODING_STR17,E.CODING_STR18,E.CODING_STR19, ");
        parser.addSQL(" E.CODING_STR20,E.RSRV_STR1,E.RSRV_STR2,E.RSRV_STR3,E.RSRV_STR4,E.RSRV_STR5,E.EC_SERIAL_NUMBER ");
        parser.addSQL(" FROM (SELECT T.IBSYSID,T.SUB_IBSYSID,T.PRODUCT_ID, ");
        parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_MEB T ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.PRODUCT_ID =:PRODUCT_ID ) R, ");
        parser.addSQL(" TF_B_EOP_QUICKORDER_MEB E ");
        parser.addSQL(" WHERE E.SUB_IBSYSID = R.SUB_IBSYSID ");
        parser.addSQL(" AND E.IBSYSID = R.IBSYSID ");
        parser.addSQL(" AND R.PRODUCT_ID = E.PRODUCT_ID ");
        parser.addSQL(" AND R.G <= 1 ");
        IDataset results = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return results;

    }

    public static int updateMemSerialNumInfo(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        params.put("RSRV_STR3", param.getString("RSRV_STR3"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_B_EOP_QUICKORDER_MEB T ");
        parser.addSQL(" SET T.EC_SERIAL_NUMBER =:SERIAL_NUMBER, T.RSRV_STR3 =:RSRV_STR3  ");
        parser.addSQL(" WHERE  T.SUB_IBSYSID = (SELECT MAX(T.SUB_IBSYSID)SUB_IBSYSID ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_MEB T ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.PRODUCT_ID =:PRODUCT_ID ) ");
        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static int updateDataSerialNumInfo(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_B_EOP_QUICKORDER_DATA T ");
        parser.addSQL(" SET T.EC_SERIAL_NUMBER =:SERIAL_NUMBER  ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.PRODUCT_ID =:PRODUCT_ID  ");
        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset getEspMembersBySerialNumber(IData param) throws Exception {
        IData params = new DataMap();
        params.put("EC_SERIAL_NUMBER", param.getString("EC_SERIAL_NUMBER"));
        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT E.SERIAL_NUMBER, ");
        parser.addSQL("        E.SUB_IBSYSID,   ");
        parser.addSQL("        E.IBSYSID,       ");
        parser.addSQL("        E.NODE_ID,       ");
        parser.addSQL("        E.ACCEPT_MONTH,  ");
        parser.addSQL("        E.CUST_ID,       ");
        parser.addSQL("        E.PRODUCT_ID,    ");
        parser.addSQL("        E.UPDATE_TIME,   ");
        parser.addSQL("        E.PRODUCT_ID,    ");
        parser.addSQL("        E.CODING_STR1,   ");
        parser.addSQL("        E.CODING_STR2,   ");
        parser.addSQL("        E.CODING_STR3,   ");
        parser.addSQL("        E.CODING_STR4,   ");
        parser.addSQL("        E.CODING_STR5,   ");
        parser.addSQL("        E.RSRV_STR1,  ");
        parser.addSQL("        E.RSRV_STR2,  ");
        parser.addSQL("        E.RSRV_STR3,  ");
        parser.addSQL("        E.RSRV_STR4,  ");
        parser.addSQL("        E.RSRV_STR5,  ");
        parser.addSQL("        E.EC_SERIAL_NUMBER ");
        parser.addSQL("   FROM TF_BH_EOP_QUICKORDER_MEB E  ");
        parser.addSQL("  WHERE E.RSRV_STR3 = '0' ");
        parser.addSQL("    AND E.EC_SERIAL_NUMBER = :EC_SERIAL_NUMBER ");
        IDataset results = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return results;
    }

    public static int updateMemStateInfoForDel(IData param) throws Exception {
        IData params = new DataMap();
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        params.put("SERIAL_NUMBER_STR", param.getString("SERIAL_NUMBER_STR"));
        params.put("RSRV_STR3", param.getString("RSRV_STR3"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_BH_EOP_QUICKORDER_MEB T ");
        parser.addSQL(" SET T.EC_SERIAL_NUMBER =:SERIAL_NUMBER, T.RSRV_STR3 =:RSRV_STR3 ");
        parser.addSQL(" WHERE T.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER IN (:SERIAL_NUMBER_STR) ");
        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static int updateMemStateInfoForAdd(IData param) throws Exception {
        IData params = new DataMap();
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        params.put("SERIAL_NUMBER_STR", param.getString("SERIAL_NUMBER_STR"));
        params.put("RSRV_STR3", param.getString("RSRV_STR3"));
        params.put("UPDATE_TIME", param.getString("UPDATE_TIME"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_BH_EOP_QUICKORDER_MEB T ");
        parser.addSQL(" SET T.EC_SERIAL_NUMBER =:SERIAL_NUMBER, T.RSRV_STR3 =:RSRV_STR3 ");
        parser.addSQL(" WHERE T.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER IN (:SERIAL_NUMBER_STR) ");
        parser.addSQL(" AND T.UPDATE_TIME <:UPDATE_TIME ");
        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryExistsRelationbbMebInfos(IData param) throws Exception {
        IData params = new DataMap();
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("SERIAL_NUMBER_B", param.getString("SERIAL_NUMBER_B"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.SERIAL_NUMBER_B  ");
        parser.addSQL("   FROM TF_F_RELATION_BB T, TF_F_USER_PRODUCT T1 ");
        parser.addSQL("  WHERE T.USER_ID_A = T1.USER_ID ");
        parser.addSQL("    AND T1.END_DATE > SYSDATE ");
        parser.addSQL("    AND T.END_DATE > SYSDATE ");
        parser.addSQL("    AND T1.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL("    AND T.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    public static IDataset qryAllMebInfosByProductId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("SERIAL_NUMBER_B", param.getString("SERIAL_NUMBER_B"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.SERIAL_NUMBER SERIAL_NUMBER_B");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_MEB T ");
        parser.addSQL(" WHERE T.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER =:SERIAL_NUMBER_B ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryAllQuickMemberInfos(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("EC_SERIAL_NUMBER", param.getString("EC_SERIAL_NUMBER"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT E.SERIAL_NUMBER, ");
        parser.addSQL("        E.SUB_IBSYSID,   ");
        parser.addSQL("        E.IBSYSID,       ");
        parser.addSQL("        E.NODE_ID,       ");
        parser.addSQL("        E.ACCEPT_MONTH,  ");
        parser.addSQL("        E.CUST_ID,       ");
        parser.addSQL("        E.PRODUCT_ID,    ");
        parser.addSQL("        E.UPDATE_TIME,   ");
        parser.addSQL("        E.PRODUCT_ID,    ");
        parser.addSQL("        E.CODING_STR1,   ");
        parser.addSQL("        E.CODING_STR2,   ");
        parser.addSQL("        E.CODING_STR3,   ");
        parser.addSQL("        E.CODING_STR4,   ");
        parser.addSQL("        E.CODING_STR5,   ");
        parser.addSQL("        E.CODING_STR6,   ");
        parser.addSQL("        E.CODING_STR7,   ");
        parser.addSQL("        E.CODING_STR8,   ");
        parser.addSQL("        E.CODING_STR9,   ");
        parser.addSQL("        E.CODING_STR10,  ");
        parser.addSQL("        E.CODING_STR11,  ");
        parser.addSQL("        E.CODING_STR12,  ");
        parser.addSQL("        E.CODING_STR13,  ");
        parser.addSQL("        E.CODING_STR14,  ");
        parser.addSQL("        E.CODING_STR15,  ");
        parser.addSQL("        E.CODING_STR16,  ");
        parser.addSQL("        E.CODING_STR17,  ");
        parser.addSQL("        E.CODING_STR18,  ");
        parser.addSQL("        E.CODING_STR19,  ");
        parser.addSQL("        E.CODING_STR20,  ");
        parser.addSQL("        E.RSRV_STR1,  ");
        parser.addSQL("        E.RSRV_STR2,  ");
        parser.addSQL("        E.RSRV_STR3,  ");
        parser.addSQL("        E.RSRV_STR4,  ");
        parser.addSQL("        E.RSRV_STR5,  ");
        parser.addSQL("        E.EC_SERIAL_NUMBER ");
        parser.addSQL(" FROM (SELECT T.IBSYSID,         ");
        parser.addSQL("              T.SUB_IBSYSID,     ");
        parser.addSQL("              T.EC_SERIAL_NUMBER, ");
        parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_MEB T  ");
        parser.addSQL(" WHERE T.IBSYSID = :IBSYSID AND T.EC_SERIAL_NUMBER = :EC_SERIAL_NUMBER ) R, ");
        parser.addSQL(" TF_B_EOP_QUICKORDER_MEB E       ");
        parser.addSQL(" WHERE E.SUB_IBSYSID = R.SUB_IBSYSID ");
        parser.addSQL("       AND E.IBSYSID = R.IBSYSID ");
        parser.addSQL("       AND R.EC_SERIAL_NUMBER = E.EC_SERIAL_NUMBER ");
        parser.addSQL("       AND R.G <= 1 ");
        IDataset results = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isEmpty(results)) {
            SQLParser parser2 = new SQLParser(param);
            parser2.addSQL(" SELECT E.SERIAL_NUMBER, ");
            parser2.addSQL("        E.SUB_IBSYSID,   ");
            parser2.addSQL("        E.IBSYSID,       ");
            parser2.addSQL("        E.NODE_ID,       ");
            parser2.addSQL("        E.ACCEPT_MONTH,  ");
            parser2.addSQL("        E.CUST_ID,       ");
            parser2.addSQL("        E.PRODUCT_ID,    ");
            parser2.addSQL("        E.UPDATE_TIME,   ");
            parser2.addSQL("        E.PRODUCT_ID,    ");
            parser2.addSQL("        E.CODING_STR1,   ");
            parser2.addSQL("        E.CODING_STR2,   ");
            parser2.addSQL("        E.CODING_STR3,   ");
            parser2.addSQL("        E.CODING_STR4,   ");
            parser2.addSQL("        E.CODING_STR5,   ");
            parser2.addSQL("        E.CODING_STR6,   ");
            parser2.addSQL("        E.CODING_STR7,   ");
            parser2.addSQL("        E.CODING_STR8,   ");
            parser2.addSQL("        E.CODING_STR9,   ");
            parser2.addSQL("        E.CODING_STR10,  ");
            parser2.addSQL("        E.CODING_STR11,  ");
            parser2.addSQL("        E.CODING_STR12,  ");
            parser2.addSQL("        E.CODING_STR13,  ");
            parser2.addSQL("        E.CODING_STR14,  ");
            parser2.addSQL("        E.CODING_STR15,  ");
            parser2.addSQL("        E.CODING_STR16,  ");
            parser2.addSQL("        E.CODING_STR17,  ");
            parser2.addSQL("        E.CODING_STR18,  ");
            parser2.addSQL("        E.CODING_STR19,  ");
            parser2.addSQL("        E.CODING_STR20,  ");
            parser2.addSQL("        E.RSRV_STR1,  ");
            parser2.addSQL("        E.RSRV_STR2,  ");
            parser2.addSQL("        E.RSRV_STR3,  ");
            parser2.addSQL("        E.RSRV_STR4,  ");
            parser2.addSQL("        E.RSRV_STR5,  ");
            parser2.addSQL("        E.EC_SERIAL_NUMBER ");
            parser2.addSQL(" FROM (SELECT T.IBSYSID,         ");
            parser2.addSQL("              T.SUB_IBSYSID,     ");
            parser2.addSQL("              T.EC_SERIAL_NUMBER, ");
            parser2.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
            parser2.addSQL(" FROM TF_BH_EOP_QUICKORDER_MEB T  ");
            parser2.addSQL(" WHERE T.IBSYSID = :IBSYSID AND T.EC_SERIAL_NUMBER = :EC_SERIAL_NUMBER ) R, ");
            parser2.addSQL(" TF_BH_EOP_QUICKORDER_MEB E       ");
            parser2.addSQL(" WHERE E.SUB_IBSYSID = R.SUB_IBSYSID ");
            parser2.addSQL("       AND E.IBSYSID = R.IBSYSID ");
            parser2.addSQL("       AND R.EC_SERIAL_NUMBER = E.EC_SERIAL_NUMBER ");
            parser2.addSQL("       AND R.G <= 1 ");
            results = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));

        }
        return results;
    }
}
