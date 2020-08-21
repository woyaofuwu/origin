package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author:YF
 * @Dat:   2013年12月20日   下午7:45:47 
 * @version: v1.0
 * @Description : 开通国际长途
 */
public class OpenINTLCallSVC extends CSBizService {
	
	public IDataset openSer(IData input) throws Exception{
		
		OpenINTLCallBean bean = (OpenINTLCallBean) BeanManager.createBean(OpenINTLCallBean.class);
		
		return bean.openSer(input,"OpenINTLCallSvc","0");
	}
	
	
	public void setTrans(IData input){
		String serial_number = input.getString("MSISDN","");
		if(!"".equals(serial_number)){
			input.put("SERIAL_NUMBER", serial_number);
		}
	}

}
