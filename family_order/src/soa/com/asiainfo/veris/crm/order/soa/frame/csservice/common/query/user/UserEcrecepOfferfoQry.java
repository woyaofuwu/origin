package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserEcrecepOfferfoQry {
    //集客大厅
    public static IDataset qryJKDTMerchInfoByMerchOfferId(String merch_offer_id) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merch_offer_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("MERCH_SPEC_CODE, ");
        sql.append("MERCH_ORDER_ID, ");
        sql.append("MERCH_OFFER_ID, ");
        sql.append("GROUP_ID, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("RSRV_NUM1, ");
        sql.append("RSRV_NUM2, ");
        sql.append("RSRV_NUM3, ");
        sql.append("to_char(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1, ");
        sql.append("RSRV_TAG2, ");
        sql.append("RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_ECRECEP_OFFER T ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.Merch_Offer_Id = :MERCH_OFFER_ID ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    public static IDataset qryJKDTMerchInfoByUserIdMerchSpecStatus(String user_id, String merch_spec_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select a.INST_ID, ");
        sql.append("a.PARTITION_ID, ");
        sql.append("to_char(a.USER_ID) USER_ID, ");
        sql.append("a.SERIAL_NUMBER, ");
        sql.append("a.MERCH_SPEC_CODE, ");
        sql.append("a.MERCH_ORDER_ID, ");
        sql.append("a.MERCH_OFFER_ID, ");
        sql.append("a.GROUP_ID, ");
        sql.append("a.OPR_SOURCE, ");
        sql.append("a.BIZ_MODE, ");
        sql.append("a.HOST_COMPANY, ");
        sql.append("to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("a.UPDATE_STAFF_ID, ");
        sql.append("a.UPDATE_DEPART_ID, ");
        sql.append("a.REMARK, ");
        sql.append("a.RSRV_NUM1, ");
        sql.append("a.RSRV_NUM2, ");
        sql.append("a.RSRV_NUM3, ");
        sql.append("to_char(a.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(a.RSRV_NUM5) RSRV_NUM5, ");
        sql.append("a.RSRV_STR1, ");
        sql.append("a.RSRV_STR2, ");
        sql.append("a.RSRV_STR3, ");
        sql.append("a.RSRV_STR4, ");
        sql.append("a.RSRV_STR5, ");
        sql.append("to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("a.RSRV_TAG1, ");
        sql.append("a.RSRV_TAG2, ");
        sql.append("a.RSRV_TAG3 ");
        sql.append("from TF_F_USER_ECRECEP_OFFER a ");
        sql.append("where a.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
}
