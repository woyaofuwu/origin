
package com.asiainfo.veris.crm.order.soa.group.cenpayspecial;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;



public class CenpayGfffUserLogQry
{
	 /**
     * 根据UserId查询记录
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGfffUserLogByUserId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" USER_ID, ");
        parser.addSQL(" INST_ID, ");
        parser.addSQL(" CUST_ID, ");
        parser.addSQL(" SYNC_DAY, ");
        parser.addSQL(" RECORD_NUM, ");
        parser.addSQL(" CONSUME_FEE, ");
        parser.addSQL(" TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID, ");
        parser.addSQL(" RSRV_NUM1, ");
        parser.addSQL(" RSRV_NUM2, ");
        parser.addSQL(" RSRV_NUM3, ");
        parser.addSQL(" RSRV_STR1, ");
        parser.addSQL(" RSRV_DATE1, ");
        parser.addSQL(" RSRV_TAG1 ");
        parser.addSQL(" FROM TF_F_USER_GFFF_LOG T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID = :USER_ID ");
        IDataset dataSet = Dao.qryByParse(parser);
        return dataSet;
    }
    
    /**
     * 根据UserId查询记录
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryAllGfffUserLogByUserId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" USER_ID, ");
        parser.addSQL(" INST_ID, ");
        parser.addSQL(" CUST_ID, ");
        parser.addSQL(" SYNC_DAY, ");
        parser.addSQL(" RECORD_NUM, ");
        parser.addSQL(" CONSUME_FEE, ");
        parser.addSQL(" TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID, ");
        parser.addSQL(" RSRV_NUM1, ");
        parser.addSQL(" RSRV_NUM2, ");
        parser.addSQL(" RSRV_NUM3, ");
        parser.addSQL(" RSRV_STR1, ");
        parser.addSQL(" RSRV_DATE1, ");
        parser.addSQL(" RSRV_TAG1 ");
        parser.addSQL(" FROM TF_F_USER_GFFF_LOG T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID = :USER_ID ");
        parser.addSQL("  AND T.START_DATE <= TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("  AND TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') <= T.END_DATE ");
        IDataset dataSet = Dao.qryByParse(parser);
        return dataSet;
    }
    
    /**
     * 根据UserId查询月份的记录
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGfffMonthLogByUserId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_F_USER_GFFF_LOG T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID = :USER_ID ");
        parser.addSQL("  AND T.SYNC_DAY = '0' ");
        //parser.addSQL("  AND T.START_DATE <= SYSDATE ");
        //parser.addSQL("  AND SYSDATE <= T.END_DATE ");
        parser.addSQL("  AND T.START_DATE <= TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("  AND TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') <= T.END_DATE ");
        IDataset dataSet = Dao.qryByParse(parser);
        return dataSet;
    }
    
    /**
     * 根据UserId查询每天的记录
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGfffDayLogByUserId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_F_USER_GFFF_LOG T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID = :USER_ID ");
        parser.addSQL("  AND T.SYNC_DAY = '1' ");
        //parser.addSQL("  AND T.START_DATE <= SYSDATE ");
        //parser.addSQL("  AND SYSDATE <= T.END_DATE ");
        parser.addSQL("  AND T.START_DATE <= TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("  AND TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') <= T.END_DATE ");
        IDataset dataSet = Dao.qryByParse(parser);
        return dataSet;
    }
    
    /**
     * 根据UserId查询每小时的记录
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGfffHourLogByUserId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_F_USER_GFFF_LOG T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID = :USER_ID ");
        parser.addSQL("  AND T.SYNC_DAY = '2' ");
        //parser.addSQL("  AND T.START_DATE <= SYSDATE ");
        //parser.addSQL("  AND SYSDATE <= T.END_DATE ");
        parser.addSQL("  AND T.START_DATE <= TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("  AND TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') <= T.END_DATE ");
        IDataset dataSet = Dao.qryByParse(parser);
        return dataSet;
    }
    
    /**
     * 修改用户的月份记录
     * @param param
     * @return
     * @throws Exception
     */
    public static int updateGfffMonthLogByUserId(IData param) 
		throws Exception
	{
	    StringBuilder sql = new StringBuilder(500);
	    sql.append("UPDATE TF_F_USER_GFFF_LOG T ");
	    sql.append(" SET T.RECORD_NUM = :RECORD_NUM, ");
	    sql.append("	T.CONSUME_FEE = :CONSUME_FEE, ");
	    sql.append("	T.UPDATE_TIME = SYSDATE ");
	    sql.append("WHERE T.PARTITION_ID = MOD(:USER_ID, 10000) ");
	    sql.append(" AND T.USER_ID = :USER_ID ");
	    sql.append(" AND T.SYNC_DAY = '0' ");
	    sql.append(" AND T.START_DATE <=TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
	    sql.append(" AND TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') <= T.END_DATE ");
	    return Dao.executeUpdate(sql, param);
	}
    
    /**
     * 修改用户的每天记录
     * @param param
     * @return
     * @throws Exception
     */
    public static int updateGfffDayLogByUserId(IData param) 
		throws Exception
	{
	    StringBuilder sql = new StringBuilder(500);
	    sql.append("UPDATE TF_F_USER_GFFF_LOG T ");
	    sql.append(" SET T.RECORD_NUM = :RECORD_NUM, ");
	    sql.append("	T.CONSUME_FEE = :CONSUME_FEE, ");
	    sql.append("	T.UPDATE_TIME = SYSDATE ");
	    sql.append("WHERE T.PARTITION_ID = MOD(:USER_ID, 10000) ");
	    sql.append(" AND T.USER_ID = :USER_ID ");
	    sql.append(" AND T.SYNC_DAY = '1' ");
	    sql.append(" AND T.START_DATE <=TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
	    sql.append(" AND TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') <= T.END_DATE ");
	    return Dao.executeUpdate(sql, param);
	}
    
    /**
     * 修改用户的每小时记录
     * @param param
     * @return
     * @throws Exception
     */
    public static int updateGfffHourLogByUserId(IData param) 
		throws Exception
	{
	    StringBuilder sql = new StringBuilder(500);
	    sql.append("UPDATE TF_F_USER_GFFF_LOG T ");
	    sql.append(" SET T.RECORD_NUM = :RECORD_NUM, ");
	    sql.append("	T.CONSUME_FEE = :CONSUME_FEE, ");
	    sql.append("	T.UPDATE_TIME = SYSDATE ");
	    sql.append("WHERE T.PARTITION_ID = MOD(:USER_ID, 10000) ");
	    sql.append(" AND T.USER_ID = :USER_ID ");
	    sql.append(" AND T.SYNC_DAY = '2' ");
	    sql.append(" AND T.START_DATE <=TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
	    sql.append(" AND TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') <= T.END_DATE ");
	    return Dao.executeUpdate(sql, param);
	}
    
    /**
     * 获取定额产品的折扣率
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData queryGfffUserAttrByUserId(String userId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	SQLParser parser = new SQLParser(param);
    	parser.addSQL("SELECT PARTITION_ID,");
    	parser.addSQL(" TO_CHAR(USER_ID) USER_ID,");
    	parser.addSQL("	INST_TYPE,");
    	parser.addSQL(" RELA_INST_ID,");
    	parser.addSQL(" INST_ID,");
    	parser.addSQL(" ATTR_CODE,");
    	parser.addSQL(" ATTR_VALUE ");
    	parser.addSQL(" FROM TF_F_USER_ATTR ");
    	parser.addSQL("WHERE PARTITION_ID = MOD(:USER_ID, 10000) ");
    	parser.addSQL(" AND USER_ID = :USER_ID ");
    	parser.addSQL(" AND ATTR_CODE = '00099002' ");
    	parser.addSQL(" AND INST_TYPE = 'D' ");
    	parser.addSQL(" AND SYSDATE BETWEEN START_DATE AND END_DATE");
        IDataset attrList = Dao.qryByParse(parser);
        if (IDataUtil.isNotEmpty(attrList))
        {
            return attrList.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }
 
    /**
     * 修改预留字段，只是为了做锁
     * @param data
     * @return
     * @throws Exception
     */
    public static  int updateForGfffMebLock(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" UPDATE TF_F_USER_GRP_GFSP T SET T.TAG_STR = 'U',T.UPDATE_TIME = SYSDATE ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.TAG_ID = 'GRPGFFFMEB_ID' ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        return Dao.executeUpdate(parser);
    }
}
