package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformProductSubBean {
	public static IData qryProductByPk(String ibsysid, String recordNum) throws Exception
	{
		IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        IDataset productInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT_SUB", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
  
        if(DataUtils.isNotEmpty(productInfos))
        {
        	return productInfos.first();
        }
        return new DataMap();
	}
	
	public static IDataset qryProductByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("VALID_TAG", EcEsopConstants.STATE_VALID);
        return Dao.qryByCode("TF_B_EOP_PRODUCT_SUB", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryProductHByIbsysid(String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        StringBuilder sql = new StringBuilder(200);
        sql.append(" SELECT * FROM TF_BH_EOP_PRODUCT_SUB ");
        sql.append(" WHERE IBSYSID = :IBSYSID ");
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }

	public static void updByIbsysid(String ibsysid, String tradeId, String userId, String serialNumber, String recordNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("TRADE_ID", tradeId);
    	param.put("USER_ID", userId);
    	param.put("SERIAL_NUMBER", serialNumber);
    	param.put("RECORD_NUM", recordNum);
    	StringBuilder sql = new StringBuilder(200);
    	
		sql.append("UPDATE TF_B_EOP_PRODUCT_SUB SET TRADE_ID = :TRADE_ID ");
        if (StringUtils.isNotEmpty(param.getString("USER_ID", ""))) {
            sql.append(" ,USER_ID = :USER_ID ");
        }
        if (StringUtils.isNotEmpty(param.getString("SERIAL_NUMBER", ""))) {
            sql.append(" ,SERIAL_NUMBER = :SERIAL_NUMBER ");
        }
        sql.append(" where IBSYSID = :IBSYSID ");
        sql.append(" AND RECORD_NUM = :RECORD_NUM ");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
	public static void updByIbsysidRecordnum(String ibsysid, String tradeId, String userId, String recordNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("TRADE_ID", tradeId);
    	param.put("USER_ID", userId);
    	param.put("RECORD_NUM", recordNum);
    	StringBuilder sql = new StringBuilder(200);
		sql.append("UPDATE TF_B_EOP_PRODUCT_EXT SET TRADE_ID = :TRADE_ID ");
        if (StringUtils.isNotEmpty(param.getString("USER_ID", ""))) {
            sql.append(" ,USER_ID = :USER_ID ");
        }
        sql.append(" where IBSYSID = :IBSYSID ");
        sql.append(" AND RECORD_NUM = :RECORD_NUM ");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
	public static void updTradeIdByIbsysid(String ibsysid, String tradeId,String recordNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("TRADE_ID", tradeId);
    	param.put("RECORD_NUM", recordNum);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT_SUB", "UPD_TRADE_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
    public static void updOrderIdByIbsysid(String ibsysid, String OrderId, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("ORDER_ID", OrderId);
        param.put("RECORD_NUM", recordNum);
        StringBuilder sql = new StringBuilder(200);
        sql.append(" UPDATE TF_B_EOP_PRODUCT_SUB SET ORDER_ID=:ORDER_ID WHERE IBSYSID=:IBSYSID AND RECORD_NUM=:RECORD_NUM ");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }

	public static void delProductByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT_SUB", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
	 public static IDataset queryHisEopProInfoByuserId(String ibsysId,String userId) throws Exception
	  {
	    	IData param = new DataMap(); 
	        param.put("IBSYSID", ibsysId);
	        param.put("USER_ID", userId);

	        StringBuilder sql = new StringBuilder(1000);
	        sql.append("SELECT T.IBSYSID,T.TRADE_ID,T.PRODUCT_TYPE_CODE,T.PRODUCT_ID, ");
	        sql.append(" T.PRODUCT_NAME, T.USER_ID, T.ACCEPT_MONTH, T.RECORD_NUM ");
	        sql.append("FROM TF_B_EOP_PRODUCT_SUB T ");
	        sql.append("WHERE T.IBSYSID = :IBSYSID ");
	        sql.append("AND T.USER_ID = :USER_ID ");
	        sql.append("UNION "); 
	        sql.append("SELECT T.IBSYSID,T.TRADE_ID,T.PRODUCT_TYPE_CODE,T.PRODUCT_ID, ");
	        sql.append(" T.PRODUCT_NAME, T.USER_ID, T.ACCEPT_MONTH, T.RECORD_NUM ");
	        sql.append("FROM TF_BH_EOP_PRODUCT_SUB T ");
	        sql.append("WHERE T.IBSYSID = :IBSYSID ");
	        sql.append("AND T.USER_ID = :USER_ID ");
	        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
	  }
}
