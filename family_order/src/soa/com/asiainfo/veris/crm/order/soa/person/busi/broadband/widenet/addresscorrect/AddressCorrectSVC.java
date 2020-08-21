package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.addresscorrect;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 地址纠错
 * @author zhengkai5
 *
 */
public class AddressCorrectSVC extends CSBizService{
	 //提交按钮
	 public void onTradeSubmit(IData input) throws Exception
	 {
		 AddressCorrectBean bean = (AddressCorrectBean) BeanManager.createBean(AddressCorrectBean.class);
		 bean.onTradeSubmit(input);
	 } 
}
