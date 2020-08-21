
package com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 畅享流量
 * 
 * @author
 */
public class LargessFluxQry
{

    /**
     * @Description:查询需要分配总流量
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryUserGrpGfffInfo(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID, ");
        parser.addSQL(" TO_CHAR(T.INST_ID) INST_ID, ");
        parser.addSQL(" TO_CHAR(T.CUST_ID) CUST_ID, ");
        parser.addSQL(" SERIAL_NUMBER, ");
        parser.addSQL(" CUST_NAME, ");
        parser.addSQL(" LIMIT_FEE, ");
        parser.addSQL(" CONSUME_FEE, ");
        parser.addSQL(" TO_CHAR(T.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME,'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID, ");
        parser.addSQL(" UPDATE_DEPART_ID, ");
        parser.addSQL(" REMARK, ");
        parser.addSQL(" RSRV_NUM1, ");
        parser.addSQL(" RSRV_NUM2, ");
        parser.addSQL(" RSRV_NUM3, ");
        parser.addSQL(" RSRV_NUM4, ");
        parser.addSQL(" RSRV_NUM5, ");
        parser.addSQL(" RSRV_STR1, ");
        parser.addSQL(" RSRV_STR2, ");
        parser.addSQL(" RSRV_STR3, ");
        parser.addSQL(" RSRV_STR4, ");
        parser.addSQL(" RSRV_STR5, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL(" RSRV_TAG1, ");
        parser.addSQL(" RSRV_TAG2, ");
        parser.addSQL(" RSRV_TAG3 ");
        parser.addSQL(" FROM TF_F_USER_GRP_GFFF T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        parser.addSQL(" ORDER BY T.INSERT_TIME ASC ");
        
        return Dao.qryByParse(parser);

    }
    
    /**
     * @Description:查询需要分配总流量
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryUserGrpGfffInfoAndRowid(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID, ");
        parser.addSQL(" TO_CHAR(T.INST_ID) INST_ID, ");
        parser.addSQL(" TO_CHAR(T.CUST_ID) CUST_ID, ");
        parser.addSQL(" SERIAL_NUMBER, ");
        parser.addSQL(" CUST_NAME, ");
        parser.addSQL(" LIMIT_FEE, ");
        parser.addSQL(" CONSUME_FEE, ");
        parser.addSQL(" TO_CHAR(T.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME,'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID, ");
        parser.addSQL(" UPDATE_DEPART_ID, ");
        parser.addSQL(" REMARK, ");
        parser.addSQL(" RSRV_NUM1, ");
        parser.addSQL(" RSRV_NUM2, ");
        parser.addSQL(" RSRV_NUM3, ");
        parser.addSQL(" RSRV_NUM4, ");
        parser.addSQL(" RSRV_NUM5, ");
        parser.addSQL(" RSRV_STR1, ");
        parser.addSQL(" RSRV_STR2, ");
        parser.addSQL(" RSRV_STR3, ");
        parser.addSQL(" RSRV_STR4, ");
        parser.addSQL(" RSRV_STR5, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL(" RSRV_TAG1, ");
        parser.addSQL(" RSRV_TAG2, ");
        parser.addSQL(" RSRV_TAG3, ");
        parser.addSQL(" ROWID ");
        parser.addSQL(" FROM TF_F_USER_GRP_GFFF T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        parser.addSQL(" ORDER BY T.INSERT_TIME ASC ");
        
        return Dao.qryByParse(parser);

    }
    
    /**
     * 分页查询需要分配总流量
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpGfffInfo(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID, ");
        parser.addSQL(" TO_CHAR(T.INST_ID) INST_ID, ");
        parser.addSQL(" TO_CHAR(T.CUST_ID) CUST_ID, ");
        parser.addSQL(" SERIAL_NUMBER, ");
        parser.addSQL(" CUST_NAME, ");
        parser.addSQL(" LIMIT_FEE, ");
        parser.addSQL(" CONSUME_FEE, ");
        parser.addSQL(" (NVL(LIMIT_FEE,0) - NVL(CONSUME_FEE,0)) SURPLUS_FEE, ");
        parser.addSQL(" TO_CHAR(T.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME,'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID, ");
        parser.addSQL(" UPDATE_DEPART_ID, ");
        parser.addSQL(" REMARK, ");
        parser.addSQL(" RSRV_NUM1, ");
        parser.addSQL(" RSRV_NUM2, ");
        parser.addSQL(" RSRV_NUM3, ");
        parser.addSQL(" RSRV_NUM4, ");
        parser.addSQL(" RSRV_NUM5, ");
        parser.addSQL(" RSRV_STR1, ");
        parser.addSQL(" RSRV_STR2, ");
        parser.addSQL(" RSRV_STR3, ");
        parser.addSQL(" RSRV_STR4, ");
        parser.addSQL(" RSRV_STR5, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL(" RSRV_TAG1, ");
        parser.addSQL(" RSRV_TAG2, ");
        parser.addSQL(" RSRV_TAG3 ");
        parser.addSQL(" FROM TF_F_USER_GRP_GFFF T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        //parser.addSQL(" AND T.START_DATE >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        //parser.addSQL(" AND T.END_DATE <= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss') ");

        return Dao.qryByParse(parser, pagination);
    }
    
    /**
     * 分页查询需要分配总流量
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpSubGfffInfo(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(T.USER_ID_A) USER_ID_A, ");
        parser.addSQL(" SERIAL_NUMBER, ");
        parser.addSQL(" TO_CHAR(T.USER_ID_B) USER_ID_B, ");
        parser.addSQL(" SERIAL_NUMBER_B, ");
        parser.addSQL(" ROLE_CODE, ");
        parser.addSQL(" CONSUME_FEE, ");
        parser.addSQL(" TO_CHAR(T.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME,'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID, ");
        parser.addSQL(" UPDATE_DEPART_ID, ");
        parser.addSQL(" REMARK, ");
        parser.addSQL(" RSRV_NUM1, ");
        parser.addSQL(" RSRV_NUM2, ");
        parser.addSQL(" RSRV_NUM3, ");
        parser.addSQL(" RSRV_STR1, ");
        parser.addSQL(" RSRV_STR2, ");
        parser.addSQL(" RSRV_STR3, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" RSRV_TAG1, ");
        parser.addSQL(" RSRV_TAG2 ");
        parser.addSQL(" FROM TF_F_USER_GRP_GFFF_SUB T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER_A ");
        parser.addSQL(" AND T.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
        //parser.addSQL(" AND T.START_DATE >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        //parser.addSQL(" AND T.END_DATE <= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        
        return Dao.qryByParse(parser, pagination);
    }
    
    /**
     * 根据号码查询总量
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpGfffAllFeeBySn(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        parser.addSQL(" SELECT SUM(NVL(T.LIMIT_FEE,0)) - SUM(NVL(T.CONSUME_FEE,0))  ALLFEE  ");
        parser.addSQL(" FROM TF_F_USER_GRP_GFFF T   ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");

        return Dao.qryByParse(parser);
    }
    
    /**
     * 查询定时任务的时间
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpGfffTagTime(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        parser.addSQL(" SELECT TO_CHAR(PARTITION_ID) PARTITION_ID, TO_CHAR(USER_ID) USER_ID , ");
        parser.addSQL(" TO_CHAR(START_DATE,'yyyy-mm-dd') START_DATE ,REMOVE_TAG, ");
        parser.addSQL(" TO_CHAR(UPDATE_TIME,'yyyy-mm-dd') UPDATE_TIME, ");
        parser.addSQL(" REMARK, RSRV_NUM1, RSRV_STR1, RSRV_DATE1, RSRV_TAG1 ");
        parser.addSQL(" FROM TF_F_USER_GRP_GFFF_TAGTIME ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND REMOVE_TAG = :REMOVE_TAG ");
        
        return Dao.qryByParse(parser);
    }
        
    /**
     * 根据号码userID修改值
     * @param data
     * @return
     * @throws Exception
     */
    public static  int updateUserGrpGfffConsume(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        parser.addSQL(" UPDATE TF_F_USER_GRP_GFFF T SET T.CONSUME_FEE = T.CONSUME_FEE  + :NUM_FEE   ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID  ");
        parser.addSQL(" AND T.INST_ID = :INST_ID ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        
        return Dao.executeUpdate(parser);
    }
    
    /**
     * 修改预留字段，只是为了做锁
     * @param data
     * @return
     * @throws Exception
     */
    public static  int updateForLock(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        parser.addSQL(" UPDATE TF_F_USER_GRP_GFFF T SET T.RSRV_STR5 = 'U',T.RSRV_DATE1 = SYSDATE ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        
        return Dao.executeUpdate(parser);
    }
    
    /**
     * 修改预留字段，只是为了做锁
     * @param data
     * @return
     * @throws Exception
     */
    public static  int updateForBatLock(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" UPDATE TF_F_USER_GRP_GFSP T SET T.TAG_STR = 'U',T.UPDATE_TIME = SYSDATE ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.TAG_ID = 'GRPGFSP_ID' ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        return Dao.executeUpdate(parser);
    }
    
}
