package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;

public class UserEcrecepMebInfoQry {
    /**
     * 根据userID和ecUserId查询TF_F_USER_ECRECEP_MEB表信息
     *
     * @author ft
     * @param user_id
     * @param grpUserId
     * @param mem_eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getSEL_BY_USERID_USERIDA(String user_id, String grpUserId, String mem_eparchy_code) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("EC_USER_ID", grpUserId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT INST_ID, ");
        parser.addSQL("to_char(USER_ID) USER_ID, ");
        parser.addSQL("SERIAL_NUMBER, ");
        parser.addSQL("SERVICE_ID, ");
        parser.addSQL("to_char(EC_USER_ID) EC_USER_ID, ");
        parser.addSQL("EC_SERIAL_NUMBER, ");
        parser.addSQL("PRODUCT_ORDER_ID, ");
        parser.addSQL("PRODUCT_OFFER_ID, ");
        parser.addSQL("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("UPDATE_STAFF_ID, ");
        parser.addSQL("UPDATE_DEPART_ID, ");
        parser.addSQL("REMARK, ");
        parser.addSQL("RSRV_NUM1, ");
        parser.addSQL("RSRV_NUM2, ");
        parser.addSQL("RSRV_NUM3, ");
        parser.addSQL("to_char(RSRV_NUM4) RSRV_NUM4, ");
        parser.addSQL("to_char(RSRV_NUM5) RSRV_NUM5, ");
        parser.addSQL("RSRV_STR1, ");
        parser.addSQL("RSRV_STR2, ");
        parser.addSQL("RSRV_STR3, ");
        parser.addSQL("RSRV_STR4, ");
        parser.addSQL("RSRV_STR5, ");
        parser.addSQL("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL("RSRV_TAG1, ");
        parser.addSQL("RSRV_TAG2, ");
        parser.addSQL("RSRV_TAG3 ");
        parser.addSQL("FROM TF_F_USER_ECRECEP_MEB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("AND EC_USER_ID = :EC_USER_ID ");
        parser.addSQL("AND USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        return Dao.qryByParse(parser, mem_eparchy_code);
    }
    
    
    public static IDataset qryEcrecepMebInfoByEcUserIdSn(String serial_number, String user_id_a,String productId) throws Exception
    {

        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productId);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("EC_USER_ID", user_id_a);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT B.USER_ID,B.SERIAL_NUMBER,B.REMOVE_TAG,B.EPARCHY_CODE,A.EC_USER_ID,A.EC_SERIAL_NUMBER,A.INST_ID,A.STATUS,A.PRODUCT_OFFER_ID ");
        parser.addSQL(" FROM TF_F_USER_ECRECEP_MEB A, TF_F_USER B ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND  A.USER_ID = B.USER_ID ");
        parser.addSQL(" AND  A.PARTITION_ID = B.PARTITION_ID ");
        parser.addSQL(" AND  A.SERIAL_NUMBER = B.SERIAL_NUMBER ");
        parser.addSQL(" AND  A.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND  A.EC_USER_ID = :EC_USER_ID");
        parser.addSQL(" AND  A.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL(" AND  SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");
        return Dao.qryByParse(parser);
    }
}
