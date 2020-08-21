package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupTwoCheckIntfSVC extends CSBizService{
	   
    private static final long serialVersionUID = 1L;
    
    

	public IDataset callBoss(IData inParam)throws Exception 
	{
		GroupTwoCheckIntfBean bean = BeanManager.createBean(GroupTwoCheckIntfBean.class);
		return bean.callBoss(inParam);
	}
	
	public IDataset failBackCallBoss(IData inParam)throws Exception 
	{
		GroupTwoCheckIntfBean bean = BeanManager.createBean(GroupTwoCheckIntfBean.class);
		return bean.failBackCallBoss(inParam);
	}
    
		public IDataset callJKDT(IData inParam)throws Exception 
	{
		GroupTwoCheckIntfBean bean = BeanManager.createBean(GroupTwoCheckIntfBean.class);
		return bean.callJKDT(inParam);
	}

}
