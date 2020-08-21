package com.asiainfo.veris.crm.order.soa.person.busi.resale;


import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author yiyb
 * 
 */
public class InterForResaleIntf extends CSBizService {

	
	private static final long serialVersionUID = 5139428345186303900L;

	public void setTrans(IData input) throws Exception{
		input.put("SERIAL_NUMBER",input.getString("MSISDN"));
	}

	public IData tradeReg(IData input) throws Exception {
		
		InterForResaleIntfBean  bean  = BeanManager.createBean(InterForResaleIntfBean.class);
		input.put("SERIAL_NUMBER",input.getString("MSISDN"));
		return bean.dealSvcOrder(input);
		
	}

}
