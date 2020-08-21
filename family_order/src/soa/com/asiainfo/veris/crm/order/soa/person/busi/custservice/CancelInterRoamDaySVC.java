package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author:YF
 * @Dat:   2013年12月23日   下午6:57:00 
 * @version: v1.0
 * @Description : TODO
 */
public class CancelInterRoamDaySVC extends CSBizService {
	
	public IDataset closeServ(IData input) throws Exception{
		
		InterRoamDayBean bean =  (InterRoamDayBean)BeanManager.createBean(InterRoamDayBean.class);
		
		return bean.clasoServ(input);
	}
	
	
	public void setTrans(IData input) {
		
		String serial_number = input.getString("MSISDN","");
		
		if(!"".equals(serial_number)){
			input.put("SERIAL_NUMBER", serial_number);
		}
		
	}

}
