package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone.order.action.finish;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction; 
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * 同步表手动插入
 */
public class InsertTiSyncTabAction implements ITradeFinishAction {

	@Override
	public void  executeAction(IData mainTrade) throws Exception 
	{ 
		String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE","");
		String userId = mainTrade.getString("USER_ID","");
		String fixNumber=mainTrade.getString("RSRV_STR1","");//固话号码
		String serialNumber = mainTrade.getString("SERIAL_NUMBER","");
		String custName =  mainTrade.getString("CUST_NAME","");
		if (tradeTypeCode!=null && "9601".equals(tradeTypeCode))
		{	
			//用户信息
			IData userInfo = UcaInfoQry.qryUserInfoByUserIdFromDB(userId, null);
			// 资源信息
	        String imsi = "0";
	        IDataset userResInfos = UserResInfoQry.queryUserResByUserIdResType(userId, "1");// 查sim卡
	        if (IDataUtil.isNotEmpty(userResInfos))// 虚拟用户是没有该资料的
	        {
	            imsi = userResInfos.getData(0).getString("IMSI", "0");
	        } 
	        //序列
	        String iv_sync_sequence = SeqMgr.getSyncIncreId();  
			String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
			
			IData infoChangeCommData = new DataMap();
			infoChangeCommData.put("SYNC_SEQUENCE", iv_sync_sequence);//序列
			infoChangeCommData.put("MODIFY_TAG", "0");//新增
			infoChangeCommData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
			infoChangeCommData.put("RELATION_TRADE_ID", mainTrade.getString("TRADE_ID"));
			infoChangeCommData.put("SYNC_DAY", syncDay);//日分区
	        infoChangeCommData.put("USER_ID", userId);
	        infoChangeCommData.put("SERIAL_NUMBER", fixNumber);
	        infoChangeCommData.put("PARTITION_ID", userId.substring(userId.length() - 4));
	        infoChangeCommData.put("IMSI", imsi);
	        infoChangeCommData.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE"));
	        infoChangeCommData.put("RELATION_TRADE_ID", mainTrade.getString("TRADE_ID"));
	        infoChangeCommData.put("UPDATE_TIME", SysDateMgr.getSysTime());
	        infoChangeCommData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
	        infoChangeCommData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
	        infoChangeCommData.put("NET_TYPE_CODE", userInfo.getString("NET_TYPE_CODE"));
	        infoChangeCommData.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 产品的开始时间
	        infoChangeCommData.put("END_DATE", SysDateMgr.getTheLastTime() );// 结束时间
	        infoChangeCommData.put("PRODUCT_ID", "-1");
	        infoChangeCommData.put("BRAND_CODE", "-1");
	        infoChangeCommData.put("INST_ID", SeqMgr.getInstId());
			Dao.insert("TI_B_USER_INFOCHANGE", infoChangeCommData, Route.getJourDbDefault());
			
			IData synchInfoData = new DataMap();
			synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence); 
			synchInfoData.put("SYNC_DAY", syncDay);
			synchInfoData.put("SYNC_TYPE", "0");
			synchInfoData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
			synchInfoData.put("STATE", "0");
			synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
			synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
			Dao.insert("TI_B_SYNCHINFO", synchInfoData, Route.getJourDbDefault());
		}
	} 
}
