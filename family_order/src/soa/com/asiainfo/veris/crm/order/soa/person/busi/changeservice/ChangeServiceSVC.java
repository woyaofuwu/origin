package com.asiainfo.veris.crm.order.soa.person.busi.changeservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangeServiceSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
	
    public IDataset loadChildInfo(IData input) throws Exception
    {   
        ChangeServiceBean changeServiceBean = (ChangeServiceBean) BeanManager.createBean(ChangeServiceBean.class); 
        IDataset result =  changeServiceBean.loadChildInfo(input);
         
         return result;
    }
 

    public IDataset submitTradeInfo(IData input) throws Exception
    {   
        ChangeServiceBean changeServiceBean = (ChangeServiceBean) BeanManager.createBean(ChangeServiceBean.class); 
        IDataset result =  changeServiceBean.submitTradeInfo(input);
         
         return result;
    }
    
    public IDataset loadFeeInfo(IData input) throws Exception
    {   
        ChangeServiceBean changeServiceBean = (ChangeServiceBean) BeanManager.createBean(ChangeServiceBean.class); 
        IDataset result =  changeServiceBean.loadFeeInfo(input);
         
         return result;
    }
   
}
