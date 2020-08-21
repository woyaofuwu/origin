package com.asiainfo.veris.crm.order.soa.person.busi.plat.crbtauthcheck;

import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;


public class UnifiedAuthenticationSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7152862831814071952L;

	public IData crbtAuth(IData param) throws Exception 
	{
		UnifiedAuthenticationBean bean = (UnifiedAuthenticationBean) BeanManager.createBean(UnifiedAuthenticationBean.class);
		IData data = bean.crbtAuth(param);
		return data;
	}
	
    @Override
	public void setTrans(IData input) throws Exception
    {
        input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", input.getString("IDVALUE")));
        super.setTrans(input);
    }
    

}
