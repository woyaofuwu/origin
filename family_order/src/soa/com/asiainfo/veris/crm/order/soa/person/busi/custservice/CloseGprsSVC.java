package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author:YF
 * @Dat:   2013年12月20日   下午8:17:39 
 * @version: v1.0
 * @Description : 取消GPRS
 */
public class CloseGprsSVC extends CSBizService{
	
	public IDataset closeGprs(IData input) throws Exception{
		ChangeGprsBean bean = (ChangeGprsBean) BeanManager.createBean(ChangeGprsBean.class);
		
		return bean.changeGprsState(input,"OpenGprsSVC","1");
	}
	
	
	
	public void setTrans(IData input){
		String serial_number = input.getString("MSISDN","");
		if(!"".equals(serial_number)){
			input.put("SERIAL_NUMBER", serial_number);
		}
	}

}
