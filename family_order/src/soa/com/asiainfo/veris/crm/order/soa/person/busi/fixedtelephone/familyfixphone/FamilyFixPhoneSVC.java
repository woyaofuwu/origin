
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.service.bean.BeanManager; 
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService; 
/**
 *  
 *  
 */
public class FamilyFixPhoneSVC extends CSBizService
{ 
    // 过滤产品类型，复机只能恢复为原有产品类型
	public IData  checkAuthSerialNum(IData param) throws Exception
    {
    	FamilyFixPhoneBean checkBean = BeanManager.createBean(FamilyFixPhoneBean.class);
    	
    	return checkBean.checkAuthSerialNum(param);
    }
    
    public IData checkFixPhoneNum(IData param)throws Exception
    {
    	FamilyFixPhoneBean checkBean = BeanManager.createBean(FamilyFixPhoneBean.class);
    	
    	return checkBean.checkFixPhoneNum(param);
    }
 
}
