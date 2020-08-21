package com.asiainfo.veris.crm.order.soa.group.esop.eopqry;


import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class EopAttrInfoBean 
{
	public static IDataset qryInfoByOperType(IData idata) throws Exception
	{
		String ibsysid = idata.getString("IBSYSID","");
		String operType = idata.getString("OPER_TYPE","");
		String recordNum = idata.getString("RECORD_NUM", "");
		IData param = new DataMap();
		param.put("IBSYSID", ibsysid);
		param.put("OPER_TYPE", operType);
		param.put("RECORD_NUM", recordNum);
		SQLParser sql = new SQLParser(param);
		sql.addSQL("select t1.attr_value,t.sub_ibsysid,t.record_num,t.group_seq,t.serialno,t.bpm_id busiform_id,t.insert_time  ");
		sql.addSQL("from tf_b_eop_eoms t, tf_b_eop_attr t1  ");
		sql.addSQL("where t.ibsysid = :IBSYSID  ");
		if(StringUtils.isNotEmpty(operType))
		{
			sql.addSQL("and t.oper_type = :OPER_TYPE ");
		}
		if(StringUtils.isNotEmpty(recordNum))
		{
			sql.addSQL("and t.record_num = :RECORD_NUM ");
		}
		sql.addSQL("and t.record_num = t1.record_num  ");
		sql.addSQL("and t.sub_ibsysid = t1.sub_ibsysid  ");
		sql.addSQL("and t.ibsysid = t1.ibsysid  ");
		sql.addSQL("and t.group_seq = t1.group_seq  ");
		sql.addSQL("and t1.attr_code = 'Content'  ");
		
		return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 根据条件查询SI工单
	 * @param pd
	 * @param idata
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qrySIInfosByCond(IData idata, Pagination pagination) throws Exception 
	{
		String ibsysid = idata.getString("IBSYSID","");
		String busiType = idata.getString("BUSI_TYPE","");
		String productId = idata.getString("PRODUCT_ID","");
		String groupId = idata.getString("GROUP_ID","");
		String custName = idata.getString("CUST_NAME","");
		String beginDate = idata.getString("BEGIN_DATE","");
		String endDate = idata.getString("END_DATE","");
		IData param = new DataMap();
		param.put("IBSYSID", ibsysid);
		param.put("BUSI_TYPE", busiType);
		param.put("PRODUCT_ID", productId);
		param.put("GROUP_ID", groupId);
		param.put("CUST_NAME", custName);
		param.put("BEGIN_DATE", beginDate);
		param.put("END_DATE", endDate);
		
		if(StringUtils.isEmpty(ibsysid))
		{
			return new DatasetList();
		}
		SQLParser sql = new SQLParser(param);
		sql.addSQL("select t1.sheettype, t3.product_name, t.ibsysid, t1.serialno, t.group_id,t1.record_num,t1.busiform_id,t1.sub_ibsysid  ");
		sql.addSQL("from tf_b_eop_subscribe t, ");
		sql.addSQL("(select t2.sheettype, t2.serialno, t2.ibsysid,t2.record_num, t2.bpm_id busiform_id, t2.sub_ibsysid ");
		sql.addSQL("from tf_b_eop_eoms t2 ");
		sql.addSQL("where t2.sub_ibsysid = (select max(b.sub_ibsysid) ");
		sql.addSQL("from tf_b_eop_eoms b ");
		sql.addSQL("where b.ibsysid = :IBSYSID  and b.RSRV_STR1 = 'SI' and (b.oper_type = 'newWorkSheet' or b.oper_type = 'renewWorkSheet')) ");
		sql.addSQL("and t2.ibsysid = :IBSYSID ");
		sql.addSQL("and t2.RSRV_STR1 = 'SI' ");
		if(StringUtils.isNotEmpty(busiType))
		{
			sql.addSQL("and t2.serialno = :BUSI_TYPE ");
		}
		sql.addSQL("and (t2.oper_type = 'newWorkSheet' or t2.oper_type = 'renewWorkSheet')) t1, ");
		sql.addSQL("tf_b_eop_product t3 ");
		sql.addSQL("where t3.record_num = '0' and t.ibsysid = t1.ibsysid and t.ibsysid = t3.ibsysid and t1.record_num = '0' ");
		sql.addSQL("and t.ibsysid = :IBSYSID ");
		if(StringUtils.isNotEmpty(productId))
		{
			sql.addSQL("and t3.product_id = :PRODUCT_ID ");
		}
		if(StringUtils.isNotEmpty(groupId))
		{
			sql.addSQL("and t.group_id = :GROUP_ID ");
		}
		if(StringUtils.isNotEmpty(custName))
		{
			sql.addSQL("and t.cust_name = :CUST_NAME ");
		}
		if(StringUtils.isNotEmpty(beginDate))
		{
			sql.addSQL("and t.accept_time > to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotEmpty(endDate))
		{
			sql.addSQL("and t.accept_time < to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		}
		
	    return Dao.qryByParse(sql, pagination, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 根据条件查询Eoms工单
	 * @param pd
	 * @param idata
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryEomsInfosByCond(IData idata, Pagination pagination) throws Exception 
	{
		String ibsysid = idata.getString("IBSYSID","");
		String busiType = idata.getString("BUSI_TYPE","");
		String productId = idata.getString("PRODUCT_ID","");
		String groupId = idata.getString("GROUP_ID","");
		String custName = idata.getString("CUST_NAME","");
		String beginDate = idata.getString("BEGIN_DATE","");
		String endDate = idata.getString("END_DATE","");
		IData param = new DataMap();
		param.put("IBSYSID", ibsysid);
		param.put("BUSI_TYPE", busiType);
		param.put("PRODUCT_ID", productId);
		param.put("GROUP_ID", groupId);
		param.put("CUST_NAME", custName);
		param.put("BEGIN_DATE", beginDate);
		param.put("END_DATE", endDate);
		
		if(StringUtils.isEmpty(ibsysid))
		{
			return new DatasetList();
		}
		SQLParser sql = new SQLParser(param);
		sql.addSQL("select t1.sheettype, t3.product_name, t.ibsysid, t1.serialno, t.group_id,t1.record_num,t1.busiform_id,t1.sub_ibsysid  ");
		sql.addSQL("from tf_b_eop_subscribe t, ");
		sql.addSQL("(select t2.sheettype, t2.serialno, t2.ibsysid,t2.record_num, t2.bpm_id busiform_id, t2.sub_ibsysid ");
		sql.addSQL("from tf_b_eop_eoms t2 ");
		sql.addSQL("where t2.sub_ibsysid = (select max(b.sub_ibsysid) ");
		sql.addSQL("from tf_b_eop_eoms b ");
		sql.addSQL("where b.ibsysid = :IBSYSID  and b.RSRV_STR1 = 'EOMS' and (b.oper_type = 'newWorkSheet' or b.oper_type = 'renewWorkSheet')) ");
		sql.addSQL("and t2.ibsysid = :IBSYSID ");
		sql.addSQL("and t2.RSRV_STR1 != 'SI' ");
		if(StringUtils.isNotEmpty(busiType))
		{
			sql.addSQL("and t2.serialno = :BUSI_TYPE ");
		}
		sql.addSQL("and (t2.oper_type = 'newWorkSheet' or t2.oper_type = 'renewWorkSheet')) t1, ");
		sql.addSQL("tf_b_eop_product t3 ");
		sql.addSQL("where t3.record_num = '0' and t.ibsysid = t1.ibsysid and t.ibsysid = t3.ibsysid and t1.record_num = '0' ");
		sql.addSQL("and t.ibsysid = :IBSYSID ");
		if(StringUtils.isNotEmpty(productId))
		{
			sql.addSQL("and t3.product_id = :PRODUCT_ID ");
		}
		if(StringUtils.isNotEmpty(groupId))
		{
			sql.addSQL("and t.group_id = :GROUP_ID ");
		}
		if(StringUtils.isNotEmpty(custName))
		{
			sql.addSQL("and t.cust_name = :CUST_NAME ");
		}
		if(StringUtils.isNotEmpty(beginDate))
		{
			sql.addSQL("and t.accept_time > to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotEmpty(endDate))
		{
			sql.addSQL("and t.accept_time < to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		}
		
	    return Dao.qryByParse(sql, pagination, Route.getJourDb(BizRoute.getRouteId()));
	}
}
