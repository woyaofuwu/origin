
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UUserInfoQry
{
    /**
     * 根据CUST_Id查询所有用户信息
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserInfoByCustId(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        StringBuilder sql = new StringBuilder(2000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID, TO_CHAR(CUST_ID) CUST_ID, ");
        sql.append("TO_CHAR(USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
        sql.append("USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
        sql.append("USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
        sql.append("PREPAY_TAG, MPUTE_MONTH_FEE, ");
        sql.append("TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
        sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
        sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
        sql.append("TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
        sql.append("IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
        sql.append("IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
        sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
        sql.append("OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
        sql.append("TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
        sql.append("DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
        sql.append("TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
        sql.append("TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
        sql.append("TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
        sql.append("TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
        sql.append("REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
        sql.append("REMOVE_REASON_CODE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
        sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3 ");
        sql.append("FROM TF_F_USER A ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND A.CUST_ID = :CUST_ID ");

        return Dao.qryBySql(sql, param);
    }

}
