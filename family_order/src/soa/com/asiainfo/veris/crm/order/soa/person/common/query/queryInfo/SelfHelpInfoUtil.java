
package com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SelfHelpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class SelfHelpInfoUtil
{
	/**
     * 查询归属业务区
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryUnionCancelInfos(IData data) throws Exception
    {
    	StringBuilder sql = new StringBuilder();
    	sql.append("SELECT a.area_code,a.area_code||'|'||a.area_name area_name FROM TD_M_AREA a ");
    	sql.append("WHERE 1=1 ");
    	sql.append(" AND a.AREA_FRAME LIKE '%'||:EPARCH_CODE||'%'");
    	return Dao.qryBySql(sql, data);
    }

    /**
     * 自助终端资料查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryTerminals(IData data, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(data);

		parser.addSQL("select * from tf_r_self_help_device t ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and t.city_code= :CITY_CODE ");
		parser.addSQL(" and t.depart_code = :DEPART_CODE ");
		parser.addSQL(" and t.device_staff_id = :DEVICE_STAFF_ID ");
		parser.addSQL(" and t.remove_tag = :REMOVE_TAG ");
		parser.addSQL(" and t.owe_tag = :OWE_TAG ");
		
		return Dao.qryByParse(parser, pagination);
    }

    /**
     * 自助终端资料单个查询
     * 
     * @param data
     * @throws Exception
     */
    public static IDataset queryTerminal(IData data, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(data);
    	
    	String queryMode = data.getString("QUERY_MODE", "");
    	if (StringUtils.equals(queryMode, "0"))
    	{
    		parser.addSQL("select * from tf_r_self_help_device t ");
    		parser.addSQL(" where 1 = 1 ");
    		parser.addSQL(" and t.depart_code = :DEPART_CODE ");
		}
    	if (StringUtils.equals(queryMode, "1"))
    	{
    		parser.addSQL("select * from tf_r_self_help_device t ");
    		parser.addSQL(" where 1 = 1 ");
    		parser.addSQL(" and t.device_staff_id = :DEVICE_STAFF_ID ");
		}
    	if (StringUtils.equals(queryMode, "2"))
    	{
    		parser.addSQL("select * from tf_r_self_help_device t ");
    		parser.addSQL(" where 1 = 1 ");
    		parser.addSQL(" and t.device_number = :DEVICE_NUMBER ");
		}
    	
    	parser.addSQL(" and t.remove_tag = '0' ");
		return Dao.qryByParse(parser, pagination);
    }
    
    public static void checkRuleBeforeSave(IData data) throws Exception
    {
    	String deviceNumber = data.getString("DEVICE_NUMBER");
		String deviceStaffId = data.getString("DEVICE_STAFF_ID");
		
    	StringBuilder sql1 = new StringBuilder();
    	sql1.append("SELECT * FROM TF_R_SELF_HELP_DEVICE T");
    	sql1.append(" WHERE T.DEVICE_NUMBER = :DEVICE_NUMBER and t.remove_tag = 0");
    	IDataset dataNumber = Dao.qryBySql(sql1, data);
    	
    	if (IDataUtil.isNotEmpty(dataNumber))
    	{
    		//设备资产编码[%s]已经使用，不能重复新增！请尝试使用其它设备资产编码。
    		CSAppException.apperr(SelfHelpException.CRM_SELFHELP_1, deviceNumber);
		}
    	
		StringBuilder sql2 = new StringBuilder();
		sql2.append("SELECT * FROM TF_R_SELF_HELP_DEVICE T");
		sql2.append(" WHERE T.DEVICE_STAFF_ID = :DEVICE_STAFF_ID and t.remove_tag = 0");
		IDataset dataDeviceStaffId = Dao.qryBySql(sql2, data);
		
		if (IDataUtil.isNotEmpty(dataDeviceStaffId))
    	{
    		//自助终端工号[%s]已经使用，不能重复新增！请尝试使用其它自助终端工号。
    		CSAppException.apperr(SelfHelpException.CRM_SELFHELP_2, deviceStaffId);
		}
    }
}
