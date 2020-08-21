package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.PricePlanCfg;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PriceCfg {

	private String priceId;
	
	private String priceName;
	
	private String fee; //统一单位为分
	
	private List<PricePlanCfg> pricePlanCfgs;

	public String getPriceId() {
		return priceId;
	}

	public void setPriceId(String priceId) {
		this.priceId = priceId;
	}

	public String getPriceName() {
		return priceName;
	}

	public void setPriceName(String priceName) {
		this.priceName = priceName;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}
	
	public List<PricePlanCfg> getPricePlanCfgs() throws Exception{
		if(this.pricePlanCfgs != null){
			return this.pricePlanCfgs;
		}
		
		this.pricePlanCfgs = new ArrayList<PricePlanCfg>();
		//调用产商品中心服务
		this.pricePlanCfgs = new ArrayList<PricePlanCfg>();		
		IDataset sp = UpcCall.queryPricePricePlanRelPricePlanByPriceId(this.priceId);	
		
		if(IDataUtil.isEmpty(sp)){
			//BizException.bizerr(CisfException.CRM_CISF_034);
		}
		for(int i=0 ,size=sp.size();i<size;i++){
			IData data = sp.getData(i);
			PricePlanCfg pricePlan = new PricePlanCfg();
			pricePlan.setCycle(data.getString("CYCLE"));
			pricePlan.setCycleUnit(data.getString("CYCLE_UNIT"));
			pricePlan.setFee(data.getString("FEE"));
			pricePlan.setFeeType(data.getString("FEE_TYPE"));
			pricePlan.setFeeTypeCode(data.getString("FEE_TYPE_CODE"));
			pricePlan.setPricePlanId(data.getString("PRICE_PLAN_ID"));
			pricePlan.setPricePlanName(data.getString("PRICE_PLAN_NAME"));
			pricePlan.setPricePlanType(data.getString("PRICE_PLAN_TYPE"));
			pricePlan.setRefBillingId(data.getString("REF_BILLING_ID"));
			this.pricePlanCfgs.add(pricePlan);
		}
		return this.pricePlanCfgs;
	}
}
