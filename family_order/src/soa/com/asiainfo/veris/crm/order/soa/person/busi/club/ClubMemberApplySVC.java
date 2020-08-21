package com.asiainfo.veris.crm.order.soa.person.busi.club;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.club.ClubMemberApplyBean;


public class ClubMemberApplySVC extends CSBizService
{
  

	public void dealScore(IData input) throws Exception
	{
	    	
		ClubMemberApplyBean bean = (ClubMemberApplyBean) BeanManager.createBean(ClubMemberApplyBean.class);
			
	    bean.dealScore(input);
	}
    
    public IDataset createApplyInfo(IData input) throws Exception
    {
    	ClubMemberApplyBean bean = (ClubMemberApplyBean) BeanManager.createBean(ClubMemberApplyBean.class);
		
    	return bean.createApplyInfo(input);
    }
    
    
   
}
