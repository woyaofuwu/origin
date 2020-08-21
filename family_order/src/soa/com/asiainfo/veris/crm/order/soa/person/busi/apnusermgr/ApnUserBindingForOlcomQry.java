package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;

public class ApnUserBindingForOlcomQry {
	
	/**
	 * 根据UserId来分页查询专用APN绑定信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserApnInfoByUserId(IData param,Pagination pagination) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT TO_CHAR(PARTITION_ID) PARTITION_ID, ");
		parser.addSQL("       TO_CHAR(USER_ID) USER_ID, ");
		parser.addSQL("       APN_NAME, ");
		parser.addSQL("       APN_DESC, ");
		parser.addSQL("       APN_CNTXID, ");
		parser.addSQL("       APN_TPLID, ");
		parser.addSQL("       APN_IPV4ADD, ");
		parser.addSQL("       APN_TYPE, ");
		parser.addSQL("       REMOVE_TAG, ");
		parser.addSQL("       TO_CHAR(INST_ID) INST_ID, ");
		parser.addSQL("       RSRV_NUM1, ");
		parser.addSQL("       RSRV_NUM2, ");
		parser.addSQL("       RSRV_NUM3, ");
		parser.addSQL("       RSRV_STR1, ");
		parser.addSQL("       RSRV_STR2, ");
		parser.addSQL("       RSRV_STR3, ");
		parser.addSQL("       RSRV_STR4, ");
		parser.addSQL("       RSRV_STR5, ");
		parser.addSQL("       RSRV_STR6, ");
		parser.addSQL("       TO_CHAR(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		parser.addSQL("       TO_CHAR(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		parser.addSQL("       RSRV_TAG1, ");
		parser.addSQL("       RSRV_TAG2, ");
		parser.addSQL("       ADD_STAFF_ID, ");
		parser.addSQL("       TO_CHAR(ADD_TIME,'yyyy-mm-dd hh24:mi:ss') ADD_TIME, ");
		parser.addSQL("       TO_CHAR(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		parser.addSQL("       UPDATE_STAFF_ID, ");
		parser.addSQL("       REMARK ");
		parser.addSQL("  FROM TF_F_USER_APN_INFO T ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
		parser.addSQL("   AND T.USER_ID = :USER_ID ");
		parser.addSQL("	  AND T.REMOVE_TAG = :REMOVE_TAG ");
	    return Dao.qryByParse(parser,pagination);
	}

	/**
	 * 根据UserId来查询专用APN绑定信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserApnInfoByUserId(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT TO_CHAR(PARTITION_ID) PARTITION_ID, ");
		parser.addSQL("       TO_CHAR(USER_ID) USER_ID, ");
		parser.addSQL("       APN_NAME, ");
		parser.addSQL("       APN_DESC, ");
		parser.addSQL("       APN_CNTXID, ");
		parser.addSQL("       APN_TPLID, ");
		parser.addSQL("       APN_IPV4ADD, ");
		parser.addSQL("       APN_TYPE, ");
		parser.addSQL("       REMOVE_TAG, ");
		parser.addSQL("       TO_CHAR(INST_ID) INST_ID ");
		parser.addSQL("  FROM TF_F_USER_APN_INFO T ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
		parser.addSQL("   AND T.USER_ID = :USER_ID ");
		parser.addSQL("	  AND T.REMOVE_TAG = :REMOVE_TAG ");
	    return Dao.qryByParse(parser);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserApnInfoByOther(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT TO_CHAR(PARTITION_ID) PARTITION_ID, ");
		parser.addSQL("       TO_CHAR(USER_ID) USER_ID, ");
		parser.addSQL("       APN_NAME, ");
		parser.addSQL("       APN_DESC, ");
		parser.addSQL("       APN_CNTXID, ");
		parser.addSQL("       APN_TPLID, ");
		parser.addSQL("       APN_IPV4ADD, ");
		parser.addSQL("       APN_TYPE, ");
		parser.addSQL("       REMOVE_TAG, ");
		parser.addSQL("       TO_CHAR(INST_ID) INST_ID ");
		parser.addSQL("  FROM TF_F_USER_APN_INFO T ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
		parser.addSQL("   AND T.USER_ID = :USER_ID ");
		parser.addSQL("	  AND T.REMOVE_TAG = :REMOVE_TAG ");
		parser.addSQL("	  AND T.APN_NAME = :APN_NAME ");
		parser.addSQL("	  AND T.APN_CNTXID = :APN_CNTXID ");
		parser.addSQL("	  AND T.APN_TPLID = :APN_TPLID ");
	    return Dao.qryByParse(parser);
	}
	
	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public static void updateUserApnInfoByInstId(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("UPDATE TF_F_USER_APN_INFO T ");
		parser.addSQL("   SET T.REMOVE_TAG      = '1', ");
		parser.addSQL("       T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
		parser.addSQL("       T.UPDATE_TIME     = SYSDATE, ");
		parser.addSQL("       T.REMARK          = :REMARK ");
		parser.addSQL(" WHERE T.PARTITION_ID = MOD(:USER_ID, 10000) ");
		parser.addSQL("   AND T.USER_ID = :USER_ID ");
		parser.addSQL("   AND T.INST_ID = :INST_ID ");
		parser.addSQL("   AND T.REMOVE_TAG = '0' ");
		Dao.executeUpdate(parser);
	}
	
    
	/**
	 * 查询专网APN的信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryAllUserApn(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT APN_NAME, ");
		parser.addSQL("       APN_DESC, ");
		parser.addSQL("       APN_LX2G_TPLID, ");
		parser.addSQL("       APN_ZX2G_TPLID, ");
		parser.addSQL("       APN_LX4G_TPLID, ");
		parser.addSQL("       APN_ZX4G_TPLID ");
		parser.addSQL("  FROM TF_F_USER_APN T ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("	  AND T.REMOVE_TAG = :REMOVE_TAG ");
	    return Dao.qryByParse(parser);
	}
	
	/**
	 * 根据APN_NAME查询专网APN的信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserApnByName(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT APN_NAME, ");
		parser.addSQL("       APN_DESC, ");
		parser.addSQL("       APN_LX2G_TPLID, ");
		parser.addSQL("       APN_ZX2G_TPLID, ");
		parser.addSQL("       APN_LX4G_TPLID, ");
		parser.addSQL("       APN_ZX4G_TPLID ");
		parser.addSQL("  FROM TF_F_USER_APN T ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND T.APN_NAME = :APN_NAME ");
		parser.addSQL("	  AND T.REMOVE_TAG = :REMOVE_TAG ");
	    return Dao.qryByParse(parser);
	}
	
}
