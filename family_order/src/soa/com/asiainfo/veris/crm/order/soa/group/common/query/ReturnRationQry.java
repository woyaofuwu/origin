
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ReturnRationQry
{
    /**
     * 返回比例录入分页查询
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupProductInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT   TO_CHAR(T.OPER_ID) OPER_ID, ");
        parser.addSQL("         TO_CHAR(T.PARTITION_ID) PARTITION_ID, ");
        parser.addSQL("         TO_CHAR(T.USER_ID) USER_ID, ");
        parser.addSQL("         T.GROUP_ID, ");
        parser.addSQL("         T.SERIAL_NUMBER, ");
        parser.addSQL("         T.RSRV_VALUE_CODE, ");
        parser.addSQL("         T.RSRV_VALUE, ");
        parser.addSQL("         T.RSRV_NUM1, ");
        parser.addSQL("         T.RSRV_NUM2, ");
        parser.addSQL("         T.RSRV_NUM3, ");
        parser.addSQL("         T.RSRV_NUM4, ");
        parser.addSQL("         T.RSRV_NUM5, ");
        parser.addSQL("         T.RSRV_STR1, ");
        parser.addSQL("         T.RSRV_STR2, ");
        parser.addSQL("         T.RSRV_STR3, ");
        parser.addSQL("         T.RSRV_STR4, ");
        parser.addSQL("         T.RSRV_STR5, ");
        parser.addSQL("         TO_CHAR(T.RSRV_DATE1, 'YYYY-MM-DD') RSRV_DATE1, ");
        parser.addSQL("         TO_CHAR(T.RSRV_DATE2, 'YYYY-MM-DD') RSRV_DATE2, ");
        parser.addSQL("         T.RSRV_DATE3, ");
        parser.addSQL("         T.RSRV_DATE4, ");
        parser.addSQL("         T.RSRV_DATE5, ");
        parser.addSQL("         T.RSRV_TAG1, ");
        parser.addSQL("         T.RSRV_TAG2, ");
        parser.addSQL("         T.RSRV_TAG3, ");
        parser.addSQL("         T.RSRV_TAG4, ");
        parser.addSQL("         T.RSRV_TAG5, ");
        parser.addSQL("         T.PROCESS_TAG, ");
        parser.addSQL("         T.STAFF_ID, ");
        parser.addSQL("         T.DEPART_ID, ");
        parser.addSQL("         T.TRADE_ID, ");
        parser.addSQL("         TO_CHAR(T.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        parser.addSQL("         TO_CHAR(T.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        parser.addSQL("         TO_CHAR(T.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        parser.addSQL("         T.UPDATE_STAFF_ID, ");
        parser.addSQL("         T.UPDATE_DEPART_ID, ");
        parser.addSQL("         T.REMARK, ");
        parser.addSQL("         T.INST_ID, ");
        parser.addSQL("         T.AREA_CODE, ");
        parser.addSQL("         T.AREA_NAME ");
        parser.addSQL("     FROM TF_F_NPRI_OTHER_LOG T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL("      AND T.GROUP_ID = :GROUP_ID  ");
        parser.addSQL("      AND T.SERIAL_NUMBER =:SERIAL_NUMBER ");
        parser.addSQL("      AND T.UPDATE_STAFF_ID >= :START_STAFF_ID ");
        parser.addSQL("      AND T.UPDATE_STAFF_ID <= :END_STAFF_ID ");
        parser.addSQL("      AND T.UPDATE_TIME >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL("      AND T.UPDATE_TIME <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL("      AND T.UPDATE_STAFF_ID LIKE ''||:CITY_CODE ||'%' ");
        return Dao.qryByParse(parser, pagination);

    }

    public static IDataset queryOther(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select * ");
        parser.addSQL("  from tf_f_user_other t ");
        parser.addSQL("  where 1 = 1 ");
        parser.addSQL("  and t.user_id=:USER_ID ");
        parser.addSQL("  and t.rsrv_value_code = 'N001'  and sysdate between t.start_date and t.end_date ");

        return Dao.qryByParse(parser);
    }

    /**
     * @Description:集团专网查询
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryProduct(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT P.PRODUCT_ID, P.* ");
        parser.addSQL("FROM TF_F_USER T, TF_F_USER_PRODUCT P ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND P.USER_ID = T.USER_ID ");
        parser.addSQL("AND P.PARTITION_ID = MOD(T.USER_ID, 10000) ");
        parser.addSQL("AND T.CUST_ID = :CUST_ID ");
        parser.addSQL("AND P.PRODUCT_ID IN ('7010', '7011', '7012', '7013', '7014', '7015') ");
        parser.addSQL("AND T.REMOVE_TAG = '0' ");
        parser.addSQL("AND P.END_DATE > SYSDATE ");

        return Dao.qryByParse(parser);

    }
}
