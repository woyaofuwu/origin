package com.asiainfo.veris.crm.order.soa.person.busi.evaluecard;

import cn.com.tass.hnyd.hsm.TassHnydAPI;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.EValueCardInfoQry;

public class ImportECRechargeInfo extends ImportTaskExecutor{

	@Override
	public IDataset executeImport(IData paramIData, IDataset datasets) throws Exception {
		// TODO Auto-generated method stub
		IDataset chargeDataList = new DatasetList();
		IDataset faildList = new DatasetList();
		IData param1 = new DataMap();
		String sn = paramIData.getString("cond_SERIAL_NUMBER");
		String channelType = paramIData.getString("CHANNEL_TYPE");
		String remark = paramIData.getString("REMARK");
		String tradeId = paramIData.getString("cond_INIT_TRADE_ID");
		String tradeStaffID = paramIData.getString("TRADE_STAFF_ID");
		String tradeDepartID = paramIData.getString("TRADE_DEPART_ID");
		String tradeCityCode = paramIData.getString("TRADE_CITY_CODE");
		String tradeEparchyCode = paramIData.getString("TRADE_EPARCHY_CODE");		
		
		String acceptMonth = SysDateMgr.getCurMonth();
		param1.put("TRADE_ID", tradeId);
		param1.put("ACCEPT_MONTH", acceptMonth);
		param1.put("SERIAL_NUMBER", sn);		
		param1.put("CHANNEL_TYPE", channelType);
		param1.put("ACCEPT_DATE", SysDateMgr.getSysDate());
		param1.put("REMARK",remark);
		param1.put("STATUS", "0");
		param1.put("TRADE_STAFF_ID", tradeStaffID);		
		param1.put("TRADE_DEPART_ID", tradeDepartID);
		param1.put("TRADE_CITY_CODE", tradeCityCode);
		param1.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
		param1.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
		for (int k = 0;k < datasets.size(); k++){			
			IData chargeDetail = new DataMap();
			String transId = getTransID();
			IData element = datasets.getData(k);
    		chargeDetail.put("TRADE_ID", tradeId);
    		chargeDetail.put("ACCEPT_MONTH", acceptMonth);
    		chargeDetail.put("TRANSACTION_ID", transId);
    		chargeDetail.put("SERIAL_NUMBER", element.getString("SERIAL_NUMBER"));
    		String initPasswd = element.getString("CARD_PASSWORD");
    		if ( !TassHnydAPI.openHsm() ) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "加密机连接失败！");
			}
    		initPasswd =  TassHnydAPI.encryptByCenterPK("2", initPasswd);
    		if ("".equals(initPasswd) || initPasswd == null) {   
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "加密机加密后卡密为空！");
			} 
    		chargeDetail.put("CARD_PASSWORD", initPasswd);
    		
    		chargeDataList.add(chargeDetail);
		}
		Dao.insert("TI_B_ELECTCARD_BAT_RECH_DETAIL", chargeDataList,Route.CONN_CRM_CEN);
		Dao.insert("TI_B_ELECTCARD_BAT_RECH", param1,Route.CONN_CRM_CEN);

		return faildList;
	}
	
	public String getTransID() throws Exception{
		String id = SeqMgr.getInstId();
		return "898"+SysDateMgr.getSysDate("yyyyMMddHHmmss")+id.substring(id.length()-6);
	}
}
