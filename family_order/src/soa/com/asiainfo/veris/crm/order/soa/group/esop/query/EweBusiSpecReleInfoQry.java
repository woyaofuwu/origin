package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class EweBusiSpecReleInfoQry 
{
	
	/**
	 * 按流程名查询产品流程配置
	 * @param bpmTempletId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryInfoByBpmTempletId(String bpmTempletId, String status) throws Exception
	{
		IData param = new DataMap();
		param.put("BPM_TEMPLET_ID", bpmTempletId);
		param.put("VALID_TAG", status);
        return Dao.qryByCode("TD_B_EWE_BUSI_SPEC_RELE", "SEL_BY_BPMTEMPLETID", param, Route.CONN_CRM_CEN);
	}

	public static IDataset qryBusiSpecReleByOperTypeProdId(String busiformOperType, String productId) throws Exception
	{
	    IData param = new DataMap();
	    param.put("BUSIFORM_OPER_TYPE", busiformOperType);
	    param.put("BUSI_CODE", productId);
	    param.put("BUSI_TYPE", "P");
	    param.put("IN_MODE_CODE", "0");
	    param.put("VALID_TAG", "0");
	    return Dao.qryByCode("TD_B_EWE_BUSI_SPEC_RELE", "SEL_BY_OFFERID_OPERTYPE_VALID", param, Route.CONN_CRM_CEN);
	}
	
	public static IDataset qryBusiSpecReleByOperType(String busiformOperType) throws Exception
	{
	    IData param = new DataMap();
	    param.put("BUSIFORM_OPER_TYPE", busiformOperType);
	    param.put("BUSI_TYPE", "P");
	    param.put("IN_MODE_CODE", "0");
	    param.put("VALID_TAG", "0");
	    return Dao.qryByCodeParser("TD_B_EWE_BUSI_SPEC_RELE", "SEL_BY_OFFERID_OPERTYPE_VALID", param, Route.CONN_CRM_CEN);
	}
	
}
