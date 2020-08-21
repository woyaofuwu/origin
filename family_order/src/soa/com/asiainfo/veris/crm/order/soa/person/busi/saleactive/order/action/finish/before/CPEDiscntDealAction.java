package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.before;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PricePlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class CPEDiscntDealAction implements ITradeAction{

	public void executeAction(BusiTradeData btd) throws Exception{
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		String tradeTypeCode = btd.getTradeTypeCode();
		String finishDate = ""; 
		String CheckStr = "0";
		if ("10".equals(tradeTypeCode))
		{
			finishDate = SysDateMgr.getSysTime(); 
		    String execDate = SysDateMgr.getSysTime();
		    String tagDate = "19";
		    String sysDay = "";
	   
            sysDay = finishDate.substring(8, 10);
            // 如大于标识日期，则下月开通
            if (sysDay.compareTo(tagDate) > 0)//测试用，之后要改回来>0
		    {
		        execDate = SysDateMgr.getDateNextMonthFirstDay(finishDate);
		    }
		
			List<DiscntTradeData> discntInfos = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			if (discntInfos == null || discntInfos.size() <= 0)
			{
			    return;
			}
			for (int i = 0; i < discntInfos.size(); i++){
				DiscntTradeData tradeDiscnt = discntInfos.get(i);
				String MODIFY_TAG = tradeDiscnt.getModifyTag();
				if ("1".equals(MODIFY_TAG)) {
					CheckStr = MODIFY_TAG;
				}
			}
			if("1".equals(CheckStr)) { 
				return ;
			} 
		
			for (int i = 0; i < discntInfos.size(); i++)
			{
				DiscntTradeData discntInfo = discntInfos.get(i);
			    String discntCode = discntInfo.getDiscntCode(); 
			    
			    if ("6481".equals(discntCode)&&sysDay.compareTo(tagDate) > 0) //测试用，之后要改回来>0
				{
			    	String endDate = discntInfo.getEndDate();
			    	//在btd取出优惠台账信息修改，并新增连带优惠，需要处理以下4个步骤
			    	//1.修改原有优惠的优惠台账;修改原有优惠的定价计划时间
			    	discntInfo.setStartDate(execDate);
			    	discntInfo.setEndDate(endDate);
			    	
			    	List<PricePlanTradeData> pricePlans = btd.getTradeDatas(TradeTableEnum.TRADE_PRICE_PLAN);
			    	if(pricePlans != null && pricePlans.size() > 0){
			    		for (int j = 0; j < pricePlans.size(); j++) {
			    			PricePlanTradeData pricePlan = pricePlans.get(j);
			    			if(pricePlan.getOfferInsId().equals(discntInfo.getInstId())){
			    				//开始时间取两者间大的，结束时间取两者间小的
			    				pricePlan.setStartDate(execDate.compareTo(pricePlan.getStartDate())>0?execDate:pricePlan.getStartDate());
			    				pricePlan.setEndDate(endDate.compareTo(pricePlan.getEndDate())>0?pricePlan.getEndDate():endDate);
			    			}
			    		}
			    	}
			    	//2.新增连带优惠的台账;新增连带优惠的定价计划(新增优惠框架会统一新增定价计划)
			    	DiscntTradeData linkDiscnt = new DiscntTradeData();
			    	linkDiscnt.setUserId(discntInfo.getUserId());
			    	linkDiscnt.setUserIdA(discntInfo.getUserIdA());
			    	linkDiscnt.setPackageId("-1");
			    	linkDiscnt.setProductId("-1");
			    	linkDiscnt.setElementId("6485");
			    	linkDiscnt.setSpecTag("0");
			    	linkDiscnt.setRelationTypeCode("");
			    	linkDiscnt.setInstId(SeqMgr.getInstId());
			    	linkDiscnt.setCampnId("");
			    	linkDiscnt.setStartDate(SysDateMgr.getSysTime());
			    	linkDiscnt.setEndDate(SysDateMgr.getLastDateThisMonth());
			    	linkDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
			    	btd.add(serialNumber, linkDiscnt);
			    	//3.判断原有优惠在offerRel台账表中有数据,如果有则修改时间;连带优惠是否要插offerRel表
			    	List<OfferRelTradeData> offerRels = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
			    	if(offerRels != null && offerRels.size() > 0){
			    		for (int j = 0; j < offerRels.size(); j++) {
			    			OfferRelTradeData offerRelOld = offerRels.get(j);
			    			if(offerRelOld.getOfferInsId().equals(discntInfo.getInstId()) || offerRelOld.getRelOfferInsId().equals(discntInfo.getInstId())){
			    				//开始时间取两者间大的，结束时间取两者间小的
			    				offerRelOld.setStartDate(execDate.compareTo(offerRelOld.getStartDate())>0?execDate:offerRelOld.getStartDate());
			    				offerRelOld.setEndDate(endDate.compareTo(offerRelOld.getEndDate())>0?offerRelOld.getEndDate():endDate);
			    			}
			    		}
			    	}
				  
			    	IDataset pricePlansNew = UpcCall.qryPricePlanInfoByOfferId(linkDiscnt.getElementId(),linkDiscnt.getElementType());
					if(IDataUtil.isNotEmpty(pricePlansNew)){
						PricePlanTradeData pptd = new PricePlanTradeData();
						pptd.setUserId(linkDiscnt.getUserId());
						pptd.setUserIdA(linkDiscnt.getUserIdA());
						pptd.setPricePlanId(pricePlansNew.getData(0).getString("PRICE_PLAN_ID"));
						pptd.setInstId(SeqMgr.getInstId());
						pptd.setBillingCode(pricePlansNew.getData(0).getString("REF_BILLING_ID"));
						pptd.setPricePlanType(pricePlansNew.getData(0).getString("PRICE_PLAN_TYPE"));
						pptd.setFeeTypeCode(pricePlansNew.getData(0).getString("FEE_TYPE_CODE"));
						pptd.setFeeType(pricePlansNew.getData(0).getString("FEE_TYPE"));
						pptd.setFee(pricePlansNew.getData(0).getString("FEE"));
						pptd.setPriceId(pricePlansNew.getData(0).getString("PRICE_ID"));
						pptd.setRelationTypeCode(linkDiscnt.getRelationTypeCode());
						pptd.setSpecTag(linkDiscnt.getSpecTag());
						pptd.setStartDate(linkDiscnt.getStartDate());
						pptd.setEndDate(linkDiscnt.getEndDate());
						pptd.setOfferInsId(linkDiscnt.getInstId());
						pptd.setOfferType(linkDiscnt.getElementType());
						pptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
						btd.add(serialNumber, pptd);
					}
					
					//4.新增连带关系
					OfferRelTradeData offerRel = new OfferRelTradeData();
					offerRel.setInstId(SeqMgr.getInstId());
					offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
					offerRel.setOfferInsId(discntInfo.getInstId());
					offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
					offerRel.setOfferCode(discntInfo.getElementId());
					offerRel.setUserId(discntInfo.getUserId());
					offerRel.setRelOfferInsId(linkDiscnt.getInstId());
					offerRel.setRelOfferCode(linkDiscnt.getElementId());
					offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
					offerRel.setRelType(BofConst.OFFER_REL_TYPE_LINK);//连带关系
					offerRel.setStartDate(linkDiscnt.getStartDate());
					offerRel.setEndDate(linkDiscnt.getEndDate());
					offerRel.setRelUserId(linkDiscnt.getUserId());
					offerRel.setGroupId("-1");
					btd.add(serialNumber, offerRel);
				} 
			} 
		}
	}
}
