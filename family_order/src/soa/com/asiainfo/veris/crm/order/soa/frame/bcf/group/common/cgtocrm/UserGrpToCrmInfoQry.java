
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.cgtocrm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGrpToCrmInfoQry
{
    public static IDataset execSYNCByTradeId(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT SYNC_SEQUENCE, ");
        parser.addSQL("SYNC_DAY ");
        parser.addSQL("FROM TI_B_USER_GRPTOCRM ");
        parser.addSQL("WHERE TRADE_ID= :TRADE_ID ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    // 获得TRADE_ID
    public static IDataset getTradeId(Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(null);
        parser.addSQL(" SELECT TRADE_ID, ");
        parser.addSQL("SYNC_SEQUENCE, ");
        parser.addSQL("SYNC_DAY, ");
        parser.addSQL("ID_TYPE, ");
        parser.addSQL("ID, ");
        parser.addSQL("STATE ");
        parser.addSQL(" FROM TI_B_USER_GRPTOCRM ");
        parser.addSQL(" WHERE TRADE_ID IS NOT NULL ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    public static IData qrySycnData(IData data) throws Exception
    {
        StringBuilder sql = new StringBuilder(500);

        sql.append(" SELECT * FROM TI_B_USER_GRPTOCRM WHERE ");
        sql.append(" SYNC_SEQUENCE = TO_NUMBER(:SYNC_SEQUENCE)	");
        sql.append(" AND SYNC_DAY = TO_NUMBER(:SYNC_DAY) ");

        IDataset iDataset = Dao.qryBySql(sql, data, Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(iDataset))
        {
            return null;
        }

        return iDataset.getData(0);
    }
    
    /**
     * 根据USER_ID查询集团所有用户信息(产品),带路由
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryAllMainProdInfoByUserIdFromDB(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
        sql.append("PRODUCT_ID, PRODUCT_MODE, BRAND_CODE, TO_CHAR(INST_ID) INST_ID, ");
        sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3, MAIN_TAG ");
        sql.append("FROM TF_F_USER_PRODUCT T ");
        sql.append("WHERE T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND T.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND T.MAIN_TAG = '1' ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        return map;
    }
    
    /**
     * 查询集团用户已所有付费关系
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryAllDefaultPayRelaByUserId(String userId, String routeId) throws Exception
    {
    	IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT  PARTITION_ID, USER_ID, ACCT_ID, PAYITEM_CODE, ACCT_PRIORITY, ");
        sql.append("USER_PRIORITY, BIND_TYPE, START_CYCLE_ID, END_CYCLE_ID, DEFAULT_TAG, ");
        sql.append("ACT_TAG, LIMIT_TYPE, LIMIT, COMPLEMENT_TAG, INST_ID, UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, UPDATE_TIME ");
        sql.append("FROM TF_A_PAYRELATION a ");
        sql.append("WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        return ids.getData(0);
    }
    
    

    // 查询TRADE_ID条件的数据信息
    public static IDataset queryInfoByTradeId(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT TRADE_ID, ");
        parser.addSQL("SYNC_SEQUENCE, ");
        parser.addSQL("ID_TYPE, ");
        parser.addSQL("ID, ");
        parser.addSQL("STATE ");
        parser.addSQL(" FROM TI_B_USER_GRPTOCRM ");
        parser.addSQL(" WHERE TRADE_ID =:TRADE_ID  ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
}
