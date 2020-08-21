
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UCustBlackInfoQry
{
    /**
     * 是否是黑名单客户
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static boolean isBlackCust(String psptTypeCode, String psptId) throws Exception
    {
        IDataset checkBlackCust = qryBlackCustInfo(psptTypeCode, psptId);

        if (IDataUtil.isEmpty(checkBlackCust))
        {
            return false;
        }

        return true;
    }

    /**
     * 根据pspt_type_code,pspt_id查询黑名单信息
     * 
     * @param psptTypeCode
     * @param psptId
     * @return
     * @throws Exception
     */
    public static IDataset qryBlackCustInfo(String psptTypeCode, String psptId) throws Exception
    {
        IData param = new DataMap();
        param.put("PSPT_TYPE_CODE", psptTypeCode);
        param.put("PSPT_ID", psptId);

        StringBuilder sql = new StringBuilder(500);

        sql.append("SELECT PSPT_TYPE_CODE, PSPT_ID, BLACK_USER_CLASS_CODE, MOB_PHONECODE, ");
        sql.append("BANK_ACCT_NO, JOIN_CAUSE, TO_CHAR(START_DATE, 'yyyy-mm-dd') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd') UPDATE_TIME, UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, REMARK ");
        sql.append("FROM TD_O_BLACKUSER ");
        sql.append("WHERE PSPT_TYPE_CODE = :PSPT_TYPE_CODE ");
        sql.append("AND PSPT_ID = :PSPT_ID ");
        sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        sql.append("AND ROWNUM = 1 ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }
    public static boolean isBlackCust(String psptId) throws Exception
    {
        IDataset checkBlackCust = qryBlackCustInfo(psptId);

        if (IDataUtil.isEmpty(checkBlackCust))
        {
            return false;
        }

        return true;
    }
    public static IDataset qryBlackCustInfo( String psptId) throws Exception
    {
        IData param = new DataMap();
        param.put("PSPT_ID", psptId);

        StringBuilder sql = new StringBuilder(500);

        sql.append("SELECT PSPT_TYPE_CODE, PSPT_ID, BLACK_USER_CLASS_CODE, MOB_PHONECODE, ");
        sql.append("BANK_ACCT_NO, JOIN_CAUSE, TO_CHAR(START_DATE, 'yyyy-mm-dd') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd') UPDATE_TIME, UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, REMARK ");
        sql.append("FROM TD_O_BLACKUSER ");
        sql.append("WHERE PSPT_ID = :PSPT_ID  ");
        sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        sql.append("AND ROWNUM = 1 ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }
}
