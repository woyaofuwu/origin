
package com.asiainfo.veris.crm.order.soa.group.outgroupsyn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 虚拟集团客户资料管理
 * 
 * @author
 */
public class ManageOutGroupSynQry
{

	/**
	 * 新增虚拟集团客户资料
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static boolean addOutGroupInfos(IData data) throws Exception {
		return Dao.insert("TF_F_CUST_GROUP_OUTSYN", data);
	}
    
	/**
	 * 查询虚拟集团客户资料
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryOutGroupSynInfo(IData param, Pagination pagination)
			throws Exception {
		SQLParser parser = new SQLParser(param);       
		parser.addSQL("SELECT T.CUST_NAME, ");
		parser.addSQL("       T.PROVINCE_CODE, ");
		parser.addSQL("       T.COUNTY_CODE, ");
		parser.addSQL("       T.EPARCHY_CODE, ");
		parser.addSQL("       T.CITY_CODE, ");
		parser.addSQL("       T.GROUP_TYPE, ");
		parser.addSQL("       T.GROUP_STATUS, ");
		parser.addSQL("       T.GROUP_ATTR, ");
		parser.addSQL("       T.ORG_TYPE_A, ");
		parser.addSQL("       T.ORG_TYPE_B, ");
		parser.addSQL("       T.ORG_TYPE_C, ");
		parser.addSQL("       T.PRO_MANAGER, ");
		parser.addSQL("       T.ENTERPRISE_SCOPE, ");
		parser.addSQL("       T.CALLING_TYPE_CODE, ");
		parser.addSQL("       T.SUB_CALLING_TYPE_CODE, ");
		parser.addSQL("       T.INDUSTRY_ATTR, ");
		parser.addSQL("       T.ENTERPRISE_TYPE_CODE, ");
		parser.addSQL("       T.BUSI_LICENCE_TYPE, ");
		parser.addSQL("       T.BUSI_LICENCE_NO, ");
		parser.addSQL("       T.LICENCE_INFO, ");
		parser.addSQL("       T.GROUP_MGR_CUST_NAME, ");
		parser.addSQL("       T.GROUP_MGR_SN, ");
		parser.addSQL("       T.CLASS_ID, ");
		parser.addSQL("       T.GROUP_ADDR, ");
		parser.addSQL("       T.POST_CODE, ");
		parser.addSQL("       T.GROUP_CONTACT_PHONE, ");
		parser.addSQL("       T.MOBILE_GRP_TAG, ");
		parser.addSQL("       T.TOP_GRP_TAG, ");
		parser.addSQL("       T.CLASS_ID_NEW, ");
		parser.addSQL("       T.EMPLOYEE_NUMS, ");
		parser.addSQL("       T.YEAR_GAIN, ");
		parser.addSQL("       T.ENTERPRISE_SIZE_CODE, ");
		parser.addSQL("       T.EXT_ISSC, ");
		parser.addSQL("       T.SECRET_GRP, ");
		parser.addSQL("       T.REMOVE_TAG, ");
		parser.addSQL("       T.IN_DATE, ");
		parser.addSQL("       T.REMARK, ");
		parser.addSQL("       T.RSRV_NUM1, ");
		parser.addSQL("       T.RSRV_NUM2, ");
		parser.addSQL("       T.RSRV_NUM3, ");
		parser.addSQL("       T.RSRV_NUM4, ");
		parser.addSQL("       T.RSRV_NUM5, ");
		parser.addSQL("       T.RSRV_NUM6, ");
		parser.addSQL("       T.RSRV_STR1, ");
		parser.addSQL("       T.RSRV_STR2, ");
		parser.addSQL("       T.RSRV_STR3, ");
		parser.addSQL("       T.RSRV_STR4, ");
		parser.addSQL("       T.RSRV_STR5, ");
		parser.addSQL("       T.RSRV_STR6, ");
		parser.addSQL("       T.RSRV_STR7, ");
		parser.addSQL("       T.RSRV_STR8, ");
		parser.addSQL("       T.RSRV_STR9, ");
		parser.addSQL("       T.RSRV_STR10, ");
		parser.addSQL("       T.RSRV_STR11, ");
		parser.addSQL("       T.RSRV_DATE1, ");
		parser.addSQL("       T.RSRV_DATE2, ");
		parser.addSQL("       T.RSRV_DATE3, ");
		parser.addSQL("       T.RSRV_DATE4, ");
		parser.addSQL("       T.RSRV_DATE5, ");
		parser.addSQL("       T.RSRV_TAG1, ");
		parser.addSQL("       T.RSRV_TAG2, ");
		parser.addSQL("       T.RSRV_TAG3, ");
		parser.addSQL("       T.RSRV_TAG4, ");
		parser.addSQL("       T.RSRV_TAG5, ");
		parser.addSQL("       T.RSRV_TAG6, ");
		parser.addSQL("       T.RSRV_TAG7, ");
		parser.addSQL("       T.RSRV_TAG8 ");
		parser.addSQL("  FROM TF_F_CUST_GROUP_OUTSYN T ");
		parser.addSQL(" WHERE T.REMOVE_TAG = '0' ");
		parser.addSQL("   AND T.CITY_CODE = :CITY_CODE ");
		parser.addSQL("   AND T.CUST_NAME LIKE '%'||:OUTSYN_CUST_NAME||'%' ");
		return Dao.qryByParse(parser, pagination);
	}
	
	/**
	 * 通过证件类型和证件号查询集团客户资料
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryCustInfoByLicenceTypeNo(IData data) throws Exception {
		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT T.BUSI_LICENCE_TYPE, ");
		parser.addSQL("       T.BUSI_LICENCE_NO, ");
		parser.addSQL("       T.CUST_ID, ");
		parser.addSQL("       T.CUST_NAME, ");
		parser.addSQL("       T.GROUP_ID ");
		parser.addSQL("  FROM TF_F_CUST_GROUP T ");
		parser.addSQL(" WHERE T.REMOVE_TAG = '0' ");
		parser.addSQL("   AND T.BUSI_LICENCE_TYPE = :BUSI_LICENCE_TYPE ");
		parser.addSQL("   AND T.BUSI_LICENCE_NO = :BUSI_LICENCE_NO ");
		return Dao.qryByParse(parser);
	}
	
	/**
	 * 通过证件类型、证件号码、集团客户名称查询虚拟集团资料
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryOutGrpByLicenceTypeNoName(IData data) throws Exception {
		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT T.BUSI_LICENCE_TYPE, ");
		parser.addSQL("       T.BUSI_LICENCE_NO, ");
		parser.addSQL("       T.CUST_NAME, ");
		parser.addSQL("       T.REMOVE_TAG ");
		parser.addSQL("  FROM TF_F_CUST_GROUP_OUTSYN T ");
		parser.addSQL(" WHERE T.REMOVE_TAG = '0' ");
		parser.addSQL("   AND T.CUST_NAME = :CUST_NAME ");
		parser.addSQL("   AND T.BUSI_LICENCE_TYPE = :BUSI_LICENCE_TYPE ");
		parser.addSQL("   AND T.BUSI_LICENCE_NO = :BUSI_LICENCE_NO ");
		return Dao.qryByParse(parser);
	}
	
	/**
	 * 通过证件类型、证件号码、集团客户名称修改虚拟集团资料
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static int saveOutGrpByLicenceTypeNoName(IData data) throws Exception{
		SQLParser parser = new SQLParser(data);
		parser.addSQL(" UPDATE TF_F_CUST_GROUP_OUTSYN T ");
		parser.addSQL("   SET T.REMOVE_TAG      = :REMOVE_TAG, ");
		parser.addSQL("       T.UPDATE_DATE     = TO_DATE(:UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss'), ");
		parser.addSQL("       T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID ");
		parser.addSQL(" WHERE T.BUSI_LICENCE_TYPE = :BUSI_LICENCE_TYPE ");
		parser.addSQL("   AND T.BUSI_LICENCE_NO = :BUSI_LICENCE_NO ");
		parser.addSQL("   AND T.CUST_NAME = :CUST_NAME ");
		parser.addSQL("   AND T.REMOVE_TAG = '0' ");
		return Dao.executeUpdate(parser);
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryOutGrpByCustName(IData data) throws Exception {
		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT T.BUSI_LICENCE_TYPE, ");
		parser.addSQL("       T.BUSI_LICENCE_NO, ");
		parser.addSQL("       T.CUST_NAME, ");
		parser.addSQL("       T.REMOVE_TAG ");
		parser.addSQL("  FROM TF_F_CUST_GROUP_OUTSYN T ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL("   AND T.REMOVE_TAG = :REMOVE_TAG ");
		parser.addSQL("   AND T.CUST_NAME = :CUST_NAME ");
		return Dao.qryByParse(parser);
	}
}
