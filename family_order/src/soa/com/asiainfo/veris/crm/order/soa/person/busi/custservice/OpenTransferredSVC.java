package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author:YF
 * @Dat:   2013年12月23日   下午8:19:10 
 * @version: v1.0
 * @Description : TODO
 */
public class OpenTransferredSVC extends CSBizService {
	
	public IDataset openServ(IData input) throws Exception{
		
		TransferredBean bean = (TransferredBean) BeanManager.createBean(TransferredBean.class);
	
		return bean.openServ(input, "TransferredSVC");
	}
	
	public void setTrans(IData input){
		
		String serial_number = input.getString("MSISDN","");
		
		if(!"".equals(serial_number)){
			input.put("SERIAL_NUMBER", serial_number);
		}
	}

}
