package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IVipServiceSVC extends CSBizService {
	
	public IDataset vipSimBakRestore(IData input) throws Exception{
		IVipServiceBean bean = (IVipServiceBean)BeanManager.createBean(IVipServiceBean.class);
		
		return bean.vipSimBakRestore(input);
	}
	
	
	
	public void setTrans(IData input){
		
		String serial_number = input.getString("MSISDN","");
		
		if(!"".equals(serial_number)){
			input.put("SERIAL_NUMBER", serial_number);
		}
	}


}
