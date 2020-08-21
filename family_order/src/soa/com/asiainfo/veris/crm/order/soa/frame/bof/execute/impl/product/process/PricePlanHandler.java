package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PricePlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PricePlanHandler {

	public static void runn(ProductModuleTradeData pmtd, BusiTradeData btd, UcaData uca) throws Exception{
		if(BofConst.MODIFY_TAG_ADD.equals(pmtd.getModifyTag())){
			createPricePlan(pmtd,btd,uca);
		}
		else if(BofConst.MODIFY_TAG_DEL.equals(pmtd.getModifyTag())){
			deletePricePlans(pmtd,btd,uca);
		}
	}
	
	private static void createPricePlan(ProductModuleTradeData pmtd, BusiTradeData btd, UcaData uca) throws Exception{
		IDataset pricePlans = UpcCall.qryPricePlanInfoByOfferId(pmtd.getElementId(),pmtd.getElementType());
		if(IDataUtil.isEmpty(pricePlans)){
			return;
		}
		DiscntTradeData discnt = (DiscntTradeData)pmtd;
		for(Object obj : pricePlans){
			IData pricePlan = (IData)obj;
			String pricePlanType = pricePlan.getString("PRICE_PLAN_TYPE");
			if(!"1".equals(pricePlanType)){
				continue;
			}
			PricePlanTradeData pptd = new PricePlanTradeData();
			pptd.setUserId(discnt.getUserId());
			pptd.setUserIdA(discnt.getUserIdA());
			pptd.setPricePlanId(pricePlan.getString("PRICE_PLAN_ID"));
			pptd.setInstId(SeqMgr.getInstId());
			pptd.setBillingCode(pricePlan.getString("REF_BILLING_ID"));
			pptd.setPricePlanType(pricePlan.getString("PRICE_PLAN_TYPE"));
			pptd.setFeeTypeCode(pricePlan.getString("FEE_TYPE_CODE"));
			pptd.setFeeType(pricePlan.getString("FEE_TYPE"));
			pptd.setFee(pricePlan.getString("FEE"));
			pptd.setPriceId(pricePlan.getString("PRICE_ID"));
			pptd.setRelationTypeCode(discnt.getRelationTypeCode());
			pptd.setSpecTag(discnt.getSpecTag());
			pptd.setStartDate(discnt.getStartDate());
			pptd.setEndDate(discnt.getEndDate());
			pptd.setOfferInsId(discnt.getInstId());
			pptd.setOfferType(pmtd.getElementType());
			pptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
			btd.add(uca.getSerialNumber(), pptd);
		}
	}
	
	private static void deletePricePlans(ProductModuleTradeData pmtd, BusiTradeData btd, UcaData uca) throws Exception{
		String offerInsId = pmtd.getInstId();
		List<PricePlanTradeData> pricePlans = uca.getPricePlansByOfferInsId(offerInsId);
		if(ArrayUtil.isEmpty(pricePlans)){
			return;
		}
		
		for(PricePlanTradeData pricePlan : pricePlans){
			pricePlan.setModifyTag(BofConst.MODIFY_TAG_DEL);
			pricePlan.setEndDate(pmtd.getEndDate());
			btd.add(uca.getSerialNumber(), pricePlan);
		}
	}
}
