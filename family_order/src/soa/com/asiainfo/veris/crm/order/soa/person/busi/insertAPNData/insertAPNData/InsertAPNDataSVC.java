package com.asiainfo.veris.crm.order.soa.person.busi.insertAPNData.insertAPNData;


import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager; 
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService; 
/**
 *  
 *  
 */
public class InsertAPNDataSVC extends CSBizService
{ 
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IData verifyInfo(IData param)throws Exception
    {
    	InsertAPNDataBean checkBean = (InsertAPNDataBean) BeanManager.createBean(InsertAPNDataBean.class);
    	
    	return checkBean.verifyInfo(param);
    }
 
  
	
}
