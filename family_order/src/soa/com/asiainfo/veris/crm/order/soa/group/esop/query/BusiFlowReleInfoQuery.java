package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class BusiFlowReleInfoQuery
{
	 public static IDataset qryBusiTypeByProductId(String operType, String productId, String areaId,String inModeCode) throws Exception
     {
    	IData data = new DataMap();
    	data.put("BUSI_TYPE", operType);
    	data.put("PROD_SPEC_ID", productId);
    	data.put("AREA_ID", areaId);
    	data.put("IN_MODE_CODE", inModeCode);
        return Dao.qryByCode("TD_B_EOP_BUSI_FLOW_RELE", "SEL_BY_PRODUCTAREA", data, Route.CONN_CRM_CEN);
    	
     }
	 
	 
	 public static IDataset qryBusiNameByTempletId(IData input) throws Exception
	 {
		 return Dao.qryByCode("TD_B_EOP_BUSI_FLOW_RELE", "SEL_BY_BPMTEMPLETID", input);
	 }
	 
	 
	 public static IDataset getOperTypeByTempletId(IData input) throws Exception
	 {
		 return Dao.qryByCode("TD_B_EOP_BUSI_FLOW_RELE", "SEL_BY_TEMPLETID", input,Route.CONN_CRM_CEN);
	 }

    public static IDataset getOperTypeByBusiCode(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT A.* FROM TD_B_EOP_BUSI_FLOW_RELE A,TD_B_EOP_PROD_SPEC B ");
        sql.addSQL(" WHERE A.BUSI_SPEC_ID=B.BUSI_SPEC_ID ");
        sql.addSQL(" AND B.PROD_SPEC_ID=:PRODUCT_ID ");
        sql.addSQL(" AND A.BUSI_CODE=:BUSI_CODE ");
        sql.addSQL(" AND A.BUSI_OPER_TYPE=:BUSI_OPER_TYPE ");
        return Dao.qryByParse(sql, Route.CONN_CRM_CEN);
    }
    
    
    public static IDataset getBusiCodeByBusitype(String busiType, String status) throws Exception
	{
		IData param = new DataMap();
		param.put("BUSI_TYPE", busiType);
		param.put("VALID_TAG", status);
        return Dao.qryByCode("TD_B_EOP_BUSI_FLOW_RELE", "SEL_BY_BUSITYPE", param, Route.CONN_CRM_CEN);
	}

    public static IDataset getBusiCodeByBusitypeAndBusiCode(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT T.* FROM TD_B_EOP_BUSI_FLOW_RELE T, TD_B_EOP_PROD_SPEC SP ");
        sql.addSQL(" WHERE T.BUSI_SPEC_ID = SP.BUSI_SPEC_ID ");
        sql.addSQL(" AND SP.PROD_SPEC_ID=:PRODUCT_ID ");
        sql.addSQL(" AND T.BUSI_TYPE = :BUSI_TYPE ");
        sql.addSQL(" AND T.BUSI_SPEC_ID = SP.BUSI_SPEC_ID ");
        sql.addSQL(" AND SYSDATE BETWEEN T.BEGIN_TIME AND T.END_TIME ");
        return Dao.qryByParse(sql, Route.CONN_CRM_CEN);
    }
    
    public static IDataset qryInfoByBpmtempletid(String bpmTempletID, String busiCode) throws Exception
    {
    	IData param = new DataMap();
    	param.put("BPM_TEMPLET_ID", bpmTempletID);
    	param.put("BUSI_CODE", busiCode);
    	param.put("VALID_TAG", "0");
    	param.put("BUSI_TYPE", "P");
    	return Dao.qryByCode("TD_B_EWE_BUSI_SPEC_RELE", "SEL_BY_BPMTEMPLETID_TYPECODE", param, Route.CONN_CRM_CEN);
   	
    }
}