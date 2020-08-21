package com.asiainfo.veris.crm.order.soa.person.busi.smsbomb;




import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class SmsBombProtectInfoQry {
	
	/**
	 * 根据号码来查询保护名称
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySmsBombProtectInfoBySn(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT RECV_ID,EXPIRE_DATE,PROV_ID ");
		parser.addSQL("  FROM TI_O_SMS_PROTEGE ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND ACCESS_NO = :SERIAL_NUMBER ");
		parser.addSQL("	  AND status='0' ");
		parser.addSQL("	  AND EXPIRE_DATE >= to_char(SYSDATE,'YYYYMMDDHH24MISS') ");
		parser.addSQL("	  ORDER BY ACCEPT_TIME DESC ");
	    return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}

	/**
	 * 根据号码来查询保护名称
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySmsBombInfoBySn(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT RECV_ID, ");
		parser.addSQL("       PROV_ID, ");
		parser.addSQL("       SERIAL_NUMBER, ");
		parser.addSQL("       UPDATE_TYPE, ");
		parser.addSQL("       TO_CHAR(EXPIRE_DATE, 'yyyyMMddHH24miss') EXPIRE_DATE, ");
		parser.addSQL("       CREATE_STAFF_ID, ");
		parser.addSQL("       TO_CHAR(CREATE_TIME, 'yyyyMMddHH24miss') CREATE_TIME, ");
		parser.addSQL("       REMOVE_TAG, ");
		parser.addSQL("       REMARK, ");
		parser.addSQL("       TO_CHAR(UPDATE_TIME, 'yyyyMMddHH24miss') UPDATE_TIME, ");
		parser.addSQL("       UPDATE_STAFF_ID, ");
		parser.addSQL("       RSRV_NUM1, ");
		parser.addSQL("       RSRV_STR1, ");
		parser.addSQL("       RSRV_STR2, ");
		parser.addSQL("       RSRV_STR3, ");
		parser.addSQL("       TO_CHAR(RSRV_DATE1, 'yyyyMMddHH24miss') RSRV_DATE1 ");
		parser.addSQL("  FROM TF_F_SMSBOMB_PROTECTINFO ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL("	  AND REMOVE_TAG = :REMOVE_TAG ");
	    return Dao.qryByParse(parser);
	}
	
	/**
	 * 批量增加保护名单
	 * @param dataSet
	 * @return
	 * @throws Exception
	 */
	public static int[] addProtectInfo(IDataset dataSet) throws Exception
	{
		return Dao.insert("TF_F_SMSBOMB_PROTECTINFO", dataSet);
	}
	
	/**
	 * 批量增加保护名单
	 * @param dataSet
	 * @return
	 * @throws Exception
	 */
	public static void addProtectInfos(IDataset dataSet) throws Exception
	{
		Dao.executeBatchByCodeCode("TF_F_SMSBOMB_PROTECTINFO", "INSERT_SMSBOMB_PROTECTINFO", dataSet);
	}
	
	/**
	 * 删除保护名单
	 * @param param
	 * @throws Exception
	 */
	public static void delProtectInfos(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("UPDATE TF_F_SMSBOMB_PROTECTINFO T ");
		parser.addSQL("   SET T.REMOVE_TAG      = :REMOVE_TAG, ");
		parser.addSQL("       T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
		parser.addSQL("       T.UPDATE_TIME     = SYSDATE, ");
		parser.addSQL("       T.UPDATE_TYPE     = :UPDATE_TYPE ");
		parser.addSQL(" WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL("   AND T.REMOVE_TAG = '0' ");
		Dao.executeUpdate(parser);
	}
	
	/**
	 * 修改保护名单
	 * @param param
	 * @throws Exception
	 */
	public static void updateProtectInfos(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("UPDATE TF_F_SMSBOMB_PROTECTINFO T ");
		parser.addSQL("   SET T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
		parser.addSQL("       T.UPDATE_TIME     = SYSDATE, ");
		parser.addSQL("       T.UPDATE_TYPE     = :UPDATE_TYPE, ");
		parser.addSQL("       T.EXPIRE_DATE     = TO_DATE(:EXPIRE_DATE, 'yyyyMMddHH24miss') ");
		parser.addSQL(" WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL("   AND T.REMOVE_TAG = '0' ");
		Dao.executeUpdate(parser);
	}
	
	/**
	 * 修改保护名单
	 * @param param
	 * @throws Exception
	 */
	public static void updateProtectInfoNo(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("UPDATE TF_F_SMSBOMB_PROTECTINFO T ");
		parser.addSQL("   SET T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
		parser.addSQL("       T.UPDATE_TIME     = SYSDATE, ");
		parser.addSQL("       T.UPDATE_TYPE     = :UPDATE_TYPE ");
		parser.addSQL(" WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL("   AND T.REMOVE_TAG = '0' ");
		Dao.executeUpdate(parser);
	}
	
	/**
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryEpareyCodeOut(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT B.*, A.ASP ");
		parser.addSQL("  FROM TD_M_MSISDN A, ");
		parser.addSQL("       (SELECT DISTINCT PROV_CODE, AREA_CODE ");
		parser.addSQL("          FROM TD_M_MSISDN ");
		parser.addSQL("         WHERE CALLED_TYPE = '1') B ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND :SERIAL_NUMBER BETWEEN A.BEGIN_MSISDN AND A.END_MSISDN ");
		parser.addSQL("   AND A.ASP = '1' ");
		parser.addSQL("   AND A.AREA_CODE = B.AREA_CODE ");
		return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	}
	
	
	/**
	 * 查询白名单
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySmsBombWhiteInfoByRecvId(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT RECV_ID, ");
		parser.addSQL("       WHITE_NUM, ");
		parser.addSQL("       WHITE_TYPE, ");
		parser.addSQL("       CREATE_STAFF_ID, ");
		parser.addSQL("       TO_CHAR(CREATE_TIME, 'yyyyMMddHH24miss') CREATE_TIME, ");
		parser.addSQL("       REMOVE_TAG, ");
		parser.addSQL("       REMARK, ");
		parser.addSQL("       TO_CHAR(UPDATE_TIME, 'yyyyMMddHH24miss') UPDATE_TIME, ");
		parser.addSQL("       UPDATE_STAFF_ID, ");
		parser.addSQL("       RSRV_NUM1, ");
		parser.addSQL("       RSRV_STR1, ");
		parser.addSQL("       RSRV_STR2 ");
		parser.addSQL("  FROM TF_F_SMSBOMB_PROTECT_SUB T ");
		parser.addSQL(" WHERE T.RECV_ID = :RECV_ID ");
		parser.addSQL("   AND T.REMOVE_TAG = :REMOVE_TAG ");
	    return Dao.qryByParse(parser);
	}
	
	/**
	 * 批量增加保护白名单
	 * @param dataSet
	 * @return
	 * @throws Exception
	 */
	public static void addProtectWhiteInfos(IDataset dataSet) throws Exception
	{
		Dao.executeBatchByCodeCode("TF_F_SMSBOMB_PROTECTINFO", "INSERT_SMSBOMB_PROTECT_SUB", dataSet);
	}
	
	/**
	 * 删除白名单
	 * @param param
	 * @throws Exception
	 */
	public static void delProtectWhiteInfo(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("UPDATE TF_F_SMSBOMB_PROTECT_SUB T ");
		parser.addSQL("   SET T.REMOVE_TAG      = :REMOVE_TAG, ");
		parser.addSQL("       T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
		parser.addSQL("       T.UPDATE_TIME     = SYSDATE ");
		parser.addSQL(" WHERE T.RECV_ID = :RECV_ID ");
		parser.addSQL("   AND T.WHITE_NUM = :WHITE_NUM ");
		parser.addSQL("   AND T.REMOVE_TAG = '0' ");
		Dao.executeUpdate(parser);
	}
	
	/**
	 * 批量删除白名单
	 * @param param
	 * @throws Exception
	 */
	public static void delProtectWhiteInfos(IDataset paramSet) throws Exception
	{
		StringBuilder parser = new StringBuilder();
		parser.append("UPDATE TF_F_SMSBOMB_PROTECT_SUB T ");
		parser.append("   SET T.REMOVE_TAG      = :REMOVE_TAG, ");
		parser.append("       T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
		parser.append("       T.UPDATE_TIME     = SYSDATE ");
		parser.append(" WHERE T.RECV_ID = :RECV_ID ");
		parser.append("   AND T.WHITE_NUM = :WHITE_NUM ");
		parser.append("   AND T.REMOVE_TAG = '0' ");
		Dao.executeBatch(parser, paramSet);
	}

	/**
	 * 删除白名单
	 * @param param
	 * @throws Exception
	 */
	public static void delProtectWhiteInfoById(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("UPDATE TF_F_SMSBOMB_PROTECT_SUB T ");
		parser.addSQL("   SET T.REMOVE_TAG      = :REMOVE_TAG, ");
		parser.addSQL("       T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
		parser.addSQL("       T.UPDATE_TIME     = SYSDATE ");
		parser.addSQL(" WHERE T.RECV_ID = :RECV_ID ");
		parser.addSQL("   AND T.REMOVE_TAG = '0' ");
		Dao.executeUpdate(parser);
	}
	
	public static void delProtectWhiteInfoByIdnew(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("UPDATE TF_F_SMSBOMB_PROTECT_SUB T ");
		parser.addSQL("   SET T.REMOVE_TAG      = :REMOVE_TAG, ");
		parser.addSQL("       T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
		parser.addSQL("       T.UPDATE_TIME     = SYSDATE ");
		parser.addSQL(" WHERE T.RECV_ID = :RECV_ID ");
		parser.addSQL("   AND T.WHITE_NUM = :WHITE_NUM ");
		parser.addSQL("   AND T.REMOVE_TAG = '0' ");
		Dao.executeUpdate(parser);
	}
	
}
