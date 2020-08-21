package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.undo;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

public class UndoDiscntReservation implements ITradeFinishAction{

	/*
	 * REQ201903120008 达量降速的解速、限速新增规则
	 * 取消指定预约优惠
	 */
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String timeSet =SysDateMgr.decodeTimestamp(SysDateMgr.getSysDateYYYYMMDDHHMMSS(),"yyyy-MM-dd");
		
		//是否存在预约产品
		IDataset productTrades = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
		for (int i = 0; i < productTrades.size(); i++) {
			String startDate =SysDateMgr.decodeTimestamp(productTrades.getData(i).getString("START_DATE"),"yyyy-MM-dd");
			if (startDate.compareTo(timeSet)>0) {
				IData inparams = new DataMap();
				 inparams.put("TRADE_ID", tradeId);
				 inparams.put("DEAL_STATE", "0");
				 inparams.put("DEAL_TYPE",BofConst.EXPIRE_TYPE_RESERVATION_SVC);
				 IDataset expireDeals=BofQuery.queryExpireDealByTradeId(inparams);
				 
				 
				 boolean isCancel =false;
				 if(IDataUtil.isNotEmpty(expireDeals)){
					 for (Iterator eit = expireDeals.iterator(); eit.hasNext();){
						 IData expireDeal = (IData) eit.next();
							// expireDeal.put("DEAL_STATE", "2");
							 //Dao.update("TF_F_EXPIRE_DEAL", expireDeal);
						 Dao.delete("TF_F_EXPIRE_DEAL", expireDeal);
						 isCancel =true;
					 }
					 
					 if (isCancel) {
						 for (int j = 0; j < expireDeals.size(); j++) {
							 
							 String userID = expireDeals.getData(j).getString("USER_ID");
							 String serialNumber = expireDeals.getData(j).getString("SERIAL_NUMBER");
							 
							 IData param = new DataMap();
								IData dealCond1 = new DataMap();
								dealCond1.put("DISCNT_CODE", expireDeals.getData(0).getString("DEAL_COND"));
					            param.put("DEAL_ID", SeqMgr.getTradeId());
					            param.put("USER_ID", userID);
					            param.put("PARTITION_ID", userID.substring(userID.length() - 4));
					            param.put("SERIAL_NUMBER", serialNumber);
					            param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
					            param.put("IN_TIME", SysDateMgr.getSysTime());
					            param.put("DEAL_STATE", "2");
					            param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_RESERVATION_SVC);
					            param.put("EXEC_TIME", startDate);
					            param.put("DEAL_COND", dealCond1.toString());
					            param.put("EXEC_MONTH", SysDateMgr.getMonthForDate(timeSet));
					            param.put("DEAL_RESULT", "预约产品取消，无须处理！");
					            param.put("TRADE_ID", tradeId);
					            
					            Dao.insert("TF_FH_EXPIRE_DEAL", param);
						}
						 
					}
					 
				 } 			
				 
			}
		}
	}
	
	

}
