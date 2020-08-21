package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserTradeCJInfoQry {

	
	/**
	 * 查询抽奖短信的下发情况
	 * @param serialNumber
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception 
	 */
	public static IDataset qryCJInfos(String serialNumber,String userId,String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		
		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT T.TRADE_ID,T.ACCEPT_MONTH,T.USER_ID,T.SERIAL_NUMBER,T.SERVICE_ID,T.ACCEPT_DATE,");
		sql.append(" T.TRADE_STAFF_ID,T.TRADE_DEPART_ID,T.UPDATE_TIME FROM  TF_B_TRADE_CJ T");
		sql.append(" WHERE T.USER_ID = :USER_ID AND T.SERIAL_NUMBER = :SERIAL_NUMBER AND T.SERVICE_ID = :SERVICE_ID");

		
		return Dao.qryBySql(sql, param, Route.getJourDb());
	}
}
