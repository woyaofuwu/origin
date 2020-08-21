package com.asiainfo.veris.crm.order.soa.person.busi.numbercard;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


public class NumberCardIntfSVC extends CSBizService
{ 
	private static final long serialVersionUID = 1L;

	/**
	 * 号码购买资格校验接口
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData chkBuyCond(IData data) throws Exception
    {
	    NumberCardIntfBean bean = BeanManager.createBean(NumberCardIntfBean.class);
	     return bean.chkBuyCond(data);
    }
}
