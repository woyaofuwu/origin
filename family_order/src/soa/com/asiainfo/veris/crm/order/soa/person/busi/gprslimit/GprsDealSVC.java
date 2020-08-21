package com.asiainfo.veris.crm.order.soa.person.busi.gprslimit;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GprsDealSVC extends CSBizService{
	
	   public void limitGprs(IData input)throws Exception{
		   GprsDealBean bean = BeanManager.createBean(GprsDealBean.class); 
	        bean.limitGprs();
	    }

}
