package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
/**
 * REQ201907010036产品、优惠预约办理在生效时触发提醒
 * @author liangdg3
 *
 */
public class BookProductSMSNoticeAction implements ITradeFinishAction {
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		 String tradeId = mainTrade.getString("TRADE_ID");
		 String userId = mainTrade.getString("USER_ID");
		 String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		 String acceptDate = mainTrade.getString("ACCEPT_DATE");
	     String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		//判断是否产品预约变更
		 IDataset productTrades = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
		 if(IDataUtil.isNotEmpty(productTrades)){
			 for (Iterator pit = productTrades.iterator(); pit.hasNext();){
				 IData productTrade = (IData) pit.next();  
				 String startDate=productTrade.getString("START_DATE");
				 boolean isProductBook = SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)
						 .compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD))>0;
				 if(isProductBook){
					 IData param = new DataMap();
					 param.put("DEAL_ID", SeqMgr.getTradeId());
				     param.put("USER_ID", userId);
				     param.put("PARTITION_ID", userId.substring(userId.length() - 4));
				     param.put("SERIAL_NUMBER", serialNumber);
				     param.put("EPARCHY_CODE", eparchyCode);
				     param.put("IN_TIME", SysDateMgr.getSysTime());
				     param.put("DEAL_STATE", "0");
				     param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_PRODUCT_BOOK);
				     param.put("EXEC_TIME", SysDateMgr.getAddHoursDate(startDate,9));
				     param.put("EXEC_MONTH", SysDateMgr.getMonthForDate(startDate));
				     param.put("TRADE_ID", tradeId);

				     IData dealCond = new DataMap();
				     dealCond.put("USER_ID", userId);
				     dealCond.put("SERIAL_NUMBER", serialNumber);
				     dealCond.put("ACCEPT_DATE",SysDateMgr.decodeTimestamp(acceptDate, SysDateMgr.PATTERN_CHINA_DATE));
				     dealCond.put("START_DATE", SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_CHINA_DATE));
                     dealCond.put("OFFER_TYPE","P" );

				     String productName = UProductInfoQry.getProductNameByProductId(productTrade.getString("PRODUCT_ID"));
				     dealCond.put("TRADE_TYPE_NAME", productName);
					 dealCond.put("OFFER_CODE", productTrade.getString("PRODUCT_ID"));
				     param.put("DEAL_COND", dealCond.toString());
				     Dao.insert("TF_F_EXPIRE_DEAL", param);
				 }
				
	          } 
			 
			//主产品变更时 同时取消预约优惠变更
			 IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
			 if(IDataUtil.isNotEmpty(discntTrades)){
				 for (Iterator dit = discntTrades.iterator(); dit.hasNext();){
					 IData discntTrade = (IData) dit.next(); 
					 String modifyTag=discntTrade.getString("MODIFY_TAG","");
					 if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
						 cancelDiscntBook(discntTrade,userId);
					 }
					 
				 }
			 }	 
			
		 }else{
			 //只预约优惠变更		 
			 IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
			 if(IDataUtil.isNotEmpty(discntTrades)){
				 for (Iterator dit = discntTrades.iterator(); dit.hasNext();){
					 IData discntTrade = (IData) dit.next();  
					 String modifyTag=discntTrade.getString("MODIFY_TAG","");
					 String discntCode=discntTrade.getString("DISCNT_CODE");
					 String startDate=discntTrade.getString("START_DATE");
					 String endDate=discntTrade.getString("END_DATE");
					 String instId=discntTrade.getString("INST_ID");
					//判断是否优惠预约变更
					 if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
						 boolean isBook = SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)
								 .compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD))>0;
						 if(isBook){
							 IData param = new DataMap();
							 param.put("DEAL_ID", SeqMgr.getTradeId());
						     param.put("USER_ID", userId);
						     param.put("PARTITION_ID", userId.substring(userId.length() - 4));
						     param.put("SERIAL_NUMBER", serialNumber);
						     param.put("EPARCHY_CODE", eparchyCode);
						     param.put("IN_TIME", SysDateMgr.getSysTime());
						     param.put("DEAL_STATE", "0");
						     param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_PRODUCT_BOOK);
						     param.put("EXEC_TIME", SysDateMgr.getAddHoursDate(startDate,9));
						     param.put("EXEC_MONTH", SysDateMgr.getMonthForDate(startDate));
						     param.put("TRADE_ID", tradeId);
						     IData dealCond = new DataMap();
						     dealCond.put("USER_ID", userId);
						     dealCond.put("SERIAL_NUMBER", serialNumber);
						     dealCond.put("ACCEPT_DATE",SysDateMgr.decodeTimestamp(acceptDate, SysDateMgr.PATTERN_CHINA_DATE));
						     dealCond.put("START_DATE", SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_CHINA_DATE));
                            dealCond.put("OFFER_TYPE","D" );
						     String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntTrade.getString("DISCNT_CODE"));
						     dealCond.put("TRADE_TYPE_NAME", discntName);
						     dealCond.put("OFFER_CODE", discntCode);
						     dealCond.put("INST_ID", instId);
						     param.put("DEAL_COND", dealCond.toString());
						     Dao.insert("TF_F_EXPIRE_DEAL", param);
						 }
						
					 }
					//判断是否取消优惠预约变更
					 if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
						 cancelDiscntBook(discntTrade,userId);
					 }
		          } 
			 }
		 }
		
	}
	/**
	 * 取消预约变更套餐
	 * @param discntTrade
	 * @throws Exception 
	 */
	private void cancelDiscntBook(IData discntTrade,String userId) throws Exception{
		 String discntCode=discntTrade.getString("DISCNT_CODE");
		 String startDate=discntTrade.getString("START_DATE");
		 String endDate=discntTrade.getString("END_DATE");
		 String instId=discntTrade.getString("INST_ID");
		 boolean isCancleBook = SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)
				 .compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD))>0;
		 //预约主产品变更时间晚于取消预约优惠变更时间   则预约优惠变更依旧生效
		 //预约主产品变更时间早于取消预约优惠变更时间 	则预约优惠变更取消  条件:endDate<startDate
		 if(isCancleBook){
			 isCancleBook=SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)
		 			 .compareTo(SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND_YYYYMMDD))<0;
		 }
		 if(isCancleBook){
			 IData inparams = new DataMap();
			 inparams.put("USER_ID", userId);
			 inparams.put("DEAL_STATE", "0");
			 inparams.put("DEAL_TYPE",BofConst.EXPIRE_TYPE_PRODUCT_BOOK);
			 IDataset expireDeals=BofQuery.queryExpireDealByUserId(inparams);
			 if(IDataUtil.isNotEmpty(expireDeals)){
				 for (Iterator eit = expireDeals.iterator(); eit.hasNext();){
					 IData expireDeal = (IData) eit.next();
					 String dealCond=expireDeal.getString("DEAL_COND");
					 if(StringUtils.isNotBlank(dealCond)&&dealCond.contains(discntCode)&&dealCond.contains(instId)){
						 Dao.delete("TF_F_EXPIRE_DEAL", expireDeal);
					 	 expireDeal.put("DEAL_STATE", "2");
						 Dao.insert("TF_FH_EXPIRE_DEAL", expireDeal);
					 }
				 }
			 }
		 }
	}
}
