package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CancelWholeNetCreditPurchasesSVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;

	public IDataset getCancelWholeNetCreditPurchasesInfo(IData input) throws Exception{
		CancelWholeNetCreditPurchasesBean bean=BeanManager.createBean(CancelWholeNetCreditPurchasesBean.class);
		return bean.getCancelWholeNetCreditPurchasesInfo(input);
	}
	 
	public IDataset getCancelRequestInfo(IData input) throws Exception{
		CancelWholeNetCreditPurchasesBean bean=BeanManager.createBean(CancelWholeNetCreditPurchasesBean.class);
		return bean.getCancelRequestInfo(input);
	}
	
	//取主产品下的构成必选优惠，或者主产品下必选组的优惠的总金额
	public IData getProductAmt(IData input) throws Exception{
		CancelWholeNetCreditPurchasesBean bean=BeanManager.createBean(CancelWholeNetCreditPurchasesBean.class);
		return bean.getProductAmt(input);
	}
	
}
