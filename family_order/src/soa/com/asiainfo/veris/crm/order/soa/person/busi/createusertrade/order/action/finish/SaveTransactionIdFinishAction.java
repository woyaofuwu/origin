package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

public class SaveTransactionIdFinishAction implements ITradeFinishAction
{

	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		// TODO Auto-generated method stub
		String tradeId = mainTrade.getString("TRADE_ID");
		String tradeTypeCode =  mainTrade.getString("TRADE_TYPE_CODE");
		String sn = mainTrade.getString("SERIAL_NUMBER");
		String custName = mainTrade.getString("CUST_NAME"); 
		String staffId = mainTrade.getString("TRADE_STAFF_ID");
		String userId = mainTrade.getString("USER_ID");

		IDataset transInfos = this.qryRealNameInfo(sn);
		if(IDataUtil.isNotEmpty(transInfos))
		{
			this.updateSyncState(sn,tradeId, tradeTypeCode,custName,staffId);

			this.updatePicInfo(sn, tradeId, tradeTypeCode, userId);
		}
		
	}
	
	public void updateSyncState(String sn,String tradeId,String tradeTypeCode,String custName,String staffId) throws Exception
	{
		IData param = new DataMap();
		param.put("STATUS", "0");
		param.put("SERIAL_NUMBER", sn);
		param.put("RSRV_STR2", tradeId+"|"+tradeTypeCode);
		param.put("UPDATE_STAFF_ID", staffId);
		param.put("CUST_NAME", custName);
		
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TF_F_REALNAME_INFO SET RSRV_TAG3 =:STATUS,RSRV_STR2 =:RSRV_STR2 ,CUST_NAME=:CUST_NAME ,UPDATE_STAFF_ID =:UPDATE_STAFF_ID ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append(" AND RSRV_TAG1 = '2' ");
        sql.append(" AND STATE = '0' ");
		Dao.executeUpdate(sql, param,Route.CONN_CRM_CEN);
	}
	
	public IDataset qryRealNameInfo(String sn) throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TRANSACTION_ID,ACCEPT_MONTH, PROV_CODE,CHANNEL_ID, SERIAL_NUMBER, CUST_NAME, PSPT_ID, STAFF_ID, VERIF_RESULT,  ");
		sql.append(" NO_PASS_TYPE,RESULT_CAUSE, PSPT_ADDR, SEX, NATION, BIRTHDAY, ISSUING_AUTHORITY, CERT_VALIDDATE,  ");
		sql.append(" CERT_EXPDATE, BUSI_TYPE, PIC_NAME_T, PIC_NAME_Z, PIC_NAME_F, STATE,  ");
		sql.append(" to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_DATE1,RSRV_DATE3, RSRV_DATE2, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, RSRV_NUM1 ");
		sql.append(" FROM TF_F_REALNAME_INFO D ");
		sql.append(" WHERE 1=1 ");
        sql.append(" AND D.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append(" AND D.RSRV_TAG1 = '2' ");
        sql.append(" AND D.STATE = '0' ");
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);
		return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
	}

	public void updatePicInfo(String sn,String tradeId,String tradeTypeCode,String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);
		param.put("TRADE_ID", tradeId);
		param.put("USER_ID", userId);
		param.put("BUSINESS_TYPE", tradeTypeCode);

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TD_B_PICTURE_INFO SET RSRV_TAG1 ='2',TRADE_ID =:TRADE_ID ,USER_ID=:USER_ID ,BUSINESS_TYPE =:BUSINESS_TYPE, UPDATE_TIME = SYSDATE ");
		sql.append(" WHERE 1=1 ");
		sql.append(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
		sql.append(" AND RSRV_TAG1 = '1' ");
		Dao.executeUpdate(sql, param,Route.CONN_CRM_CEN);
	}

}
