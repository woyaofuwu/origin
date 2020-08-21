
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.reg;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PricePlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;

public class BirdDiscntDateAction implements ITradeAction
{

	/**
	 * 
	 * 
	 * @author zhangxing
	 */
	public void executeAction(BusiTradeData btd) throws Exception {

		String endDate = "";
		String startDate = "";
		String instId_84003842 = "";
		String instId_84003843 = "";
		String instId_84003439 = "";
		// 1.处理discntTrade表
		String rsrvStr1 = btd.getMainTradeData().getRsrvStr1();
		if(rsrvStr1 != null && "66000206".equals(rsrvStr1))
		{
			List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			int size = discntTradeDatas.size();
			for (int i = 0; i < size; i++) {
				DiscntTradeData discntTradeData = discntTradeDatas.get(i);
				String discntCode = discntTradeData.getDiscntCode();
				startDate = discntTradeData.getStartDate();

				// 候鸟减免手机停机保号费套餐  84003842
				if ("84003842".equals(discntCode)) {

					instId_84003842 = discntTradeData.getInstId();
					endDate = SysDateMgr.getAddMonthsLastDay(12, startDate);
					discntTradeData.setEndDate(endDate);
				}
				
				// 候鸟减免宽带魔百和停机保号费套餐  84003843
				if ("84003843".equals(discntCode)) {
					instId_84003843 = discntTradeData.getInstId();
					endDate = SysDateMgr.END_DATE_FOREVER;

					discntTradeData.setEndDate(endDate);

				}
				
				// 候鸟减免50M宽带套餐    84003439
				if ("84003439".equals(discntCode)) {
					instId_84003439 = discntTradeData.getInstId();
					endDate = SysDateMgr.END_DATE_FOREVER;

					discntTradeData.setEndDate(endDate);

				}

			}

			// 2.处理TRADE_OFFER_REL表
			List<OfferRelTradeData> offerRelTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
			for (OfferRelTradeData offerRelTradeData : offerRelTradeDatas) 
			{

				String relOfferInsId = offerRelTradeData.getRelOfferInsId();
				startDate = offerRelTradeData.getStartDate();
				
				
				//候鸟减免手机停机保号费套餐      
				if (instId_84003842.equals(relOfferInsId)) {
					endDate = SysDateMgr.getAddMonthsLastDay(12, startDate);
					offerRelTradeData.setEndDate(endDate);

				}
				// 候鸟减免宽带魔百和停机保号费套餐
				if (instId_84003843.equals(relOfferInsId)) {
					endDate = SysDateMgr.END_DATE_FOREVER;
					offerRelTradeData.setEndDate(endDate);
					
				}
				
				// 候鸟减免50M宽带套餐  
				if (instId_84003439.equals(relOfferInsId)) {
					endDate = SysDateMgr.END_DATE_FOREVER;
					offerRelTradeData.setEndDate(endDate);
					
				}

			}

			// 3.处理priceplantrade表
			List<PricePlanTradeData> pricePlanTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PRICE_PLAN);
			for (PricePlanTradeData pricePlanTradeData : pricePlanTradeDatas) {
				String offerInsId = pricePlanTradeData.getOfferInsId();
				startDate = pricePlanTradeData.getStartDate();
				
				
				// 候鸟减免手机停机保号费套餐  
				if (instId_84003842.equals(offerInsId)) {
					endDate = SysDateMgr.getAddMonthsLastDay(12, startDate);
					pricePlanTradeData.setEndDate(endDate);

				}
				// 候鸟减免宽带魔百和停机保号费套餐
				if (instId_84003843.equals(offerInsId)) {
					
					endDate = SysDateMgr.END_DATE_FOREVER;
					
					pricePlanTradeData.setEndDate(endDate);
					
				}
				// 候鸟减免宽带魔百和停机保号费套餐
				if (instId_84003439.equals(offerInsId)) {
					
					endDate = SysDateMgr.END_DATE_FOREVER;
					
					pricePlanTradeData.setEndDate(endDate);
					
				}

			}
		}
		
		// 度假宽带季度套餐2019处理discntTrade表
		if(rsrvStr1 != null && "66004809".equals(rsrvStr1))
		{
			String serialNumber = btd.getRD().getUca().getSerialNumber();
			String userId = btd.getRD().getUca().getUserId();
            String startDateStr = SysDateMgr.getSysDate();
			String saleEndDateStr = "";
			List<SaleActiveTradeData> saleTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
			for (int i = 0; i < saleTradeDatas.size(); i++) {
				SaleActiveTradeData saleActiveTradeData = saleTradeDatas.get(i);
				if ("66004809".equals(saleActiveTradeData.getProductId()) && "0".equals(saleActiveTradeData.getModifyTag()))
				{
					startDateStr = saleActiveTradeData.getStartDate();
					saleEndDateStr = saleActiveTradeData.getEndDate();
				}
			}
			String endDateStr = SysDateMgr.getAddMonthsLastDay(12, startDateStr);
			
			List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			int size = discntTradeDatas.size();
			for (int i = 0; i < size; i++) {
				DiscntTradeData updDiscntTD = discntTradeDatas.get(i);	
	        	updDiscntTD.setStartDate(startDateStr);
	        	updDiscntTD.setEndDate(saleEndDateStr);
			}
			
			// 1.度假宽带2019 处理手机停机保号费套餐：84003842
            List<DiscntTradeData> discntTradeDatas1 = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray("84003842");
			if (discntTradeDatas1.isEmpty())
			{
				DiscntTradeData newDiscnt = new DiscntTradeData();
	        	newDiscnt.setElementId("84003842");        	
	        	newDiscnt.setUserId(userId);
	            newDiscnt.setUserIdA("-1");
	            newDiscnt.setProductId("-1");
	            newDiscnt.setPackageId("-1");
	        	newDiscnt.setInstId(SeqMgr.getInstId());
	            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
	            newDiscnt.setSpecTag("0");
	            newDiscnt.setStartDate(startDateStr);
	            newDiscnt.setEndDate(endDateStr);
	            newDiscnt.setRemark("度假宽带2019:绑定减免手机停机保号费套餐 ！");
	            btd.add(serialNumber, newDiscnt);
			}
			else 
			{
				DiscntTradeData modDiscntTD = discntTradeDatas1.get(0).clone();
				modDiscntTD.setEndDate(endDateStr);
	        	modDiscntTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
	        	modDiscntTD.setRemark("度假宽带2019:重新计算减免手机停机保号费套餐结束时间！");				          				
				btd.add(btd.getRD().getUca().getSerialNumber(), modDiscntTD);
			}
            
			// 2.度假宽带2019 处理宽带停机保号费套餐：84071456
			List<DiscntTradeData> discntTradeDatas2 = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray("84071456");
			if (discntTradeDatas2.isEmpty())
			{
				DiscntTradeData newDiscnt2 = new DiscntTradeData();
				newDiscnt2.setElementId("84071456");        	
				newDiscnt2.setUserId(userId);
				newDiscnt2.setUserIdA("-1");
				newDiscnt2.setProductId("-1");
				newDiscnt2.setPackageId("-1");
				newDiscnt2.setInstId(SeqMgr.getInstId());
				newDiscnt2.setModifyTag(BofConst.MODIFY_TAG_ADD);
				newDiscnt2.setSpecTag("0");
	            newDiscnt2.setStartDate(startDateStr);
	            newDiscnt2.setEndDate(endDateStr);
	            newDiscnt2.setRemark("度假宽带2019:绑定减免宽带停机保号费套餐 ！");
	            btd.add(serialNumber, newDiscnt2);
			}
			else
			{
				DiscntTradeData modDiscntTD2 = discntTradeDatas2.get(0).clone();
				modDiscntTD2.setEndDate(endDateStr);
				modDiscntTD2.setModifyTag(BofConst.MODIFY_TAG_UPD);
				modDiscntTD2.setRemark("度假宽带2019:重新计算减免宽带停机保号费套餐结束时间！");				          				
				btd.add(btd.getRD().getUca().getSerialNumber(), modDiscntTD2);
			}
			
			// 3.度假宽带2019 处理度假宽带40元月功能费:84071446
			List<DiscntTradeData> discntTradeDatas3 = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray("84071446");
			if (!discntTradeDatas3.isEmpty())
			{
				DiscntTradeData modDiscntTD3 = discntTradeDatas3.get(0).clone();
				modDiscntTD3.setEndDate(SysDateMgr.getAddMonthsLastDay(0, startDateStr));
				modDiscntTD3.setModifyTag(BofConst.MODIFY_TAG_DEL);
				modDiscntTD3.setRemark("度假宽带2019:如果存在度假宽带40元月功能费，则终止！");				          				
				btd.add(btd.getRD().getUca().getSerialNumber(), modDiscntTD3);
			}
		}
		
	}

}
