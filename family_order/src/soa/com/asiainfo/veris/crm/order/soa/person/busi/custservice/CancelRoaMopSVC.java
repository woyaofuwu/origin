package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author:YF
 * @Dat:   2013年12月20日   下午7:33:56 
 * @version: v1.0
 * @Description : TODO
 */
public class CancelRoaMopSVC extends CSBizService {
	
	
	public IDataset cancelRoaMop(IData input) throws Exception{
		
		CancelRoaMopBean bean = (CancelRoaMopBean) BeanManager.createBean(CancelRoaMopBean.class);
		
		return bean.cancelRoaMop(input,"ChangeProductRoaMopSVC");
	}
	
	
	public void setTrans(IData input){
		String serial_number = input.getString("MSISDN","");
		
		if(!"".equals(serial_number)){
			input.put("SERIAL_NUMBER", serial_number);
		}
	}

}
