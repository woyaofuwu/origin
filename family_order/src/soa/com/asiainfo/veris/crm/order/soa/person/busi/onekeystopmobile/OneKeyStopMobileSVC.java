package com.asiainfo.veris.crm.order.soa.person.busi.onekeystopmobile;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class OneKeyStopMobileSVC  extends CSBizService
{
    public IDataset queryCustInfo(IData input) throws Exception{
        OneKeyStopMobileBean bean = (OneKeyStopMobileBean) BeanManager.createBean(OneKeyStopMobileBean.class);
        return bean.queryCustInfo(input);      
    }
    
    public IDataset queryUserInfo(IData input) throws Exception{
        OneKeyStopMobileBean bean = (OneKeyStopMobileBean) BeanManager.createBean(OneKeyStopMobileBean.class);
        return bean.queryUserInfo(input);      
    }
    
    public IDataset queryLastTrade(IData input) throws Exception{
        OneKeyStopMobileBean bean = (OneKeyStopMobileBean) BeanManager.createBean(OneKeyStopMobileBean.class);
        return bean.queryLastTrade(input);      
    }
    
    public IDataset queryUUInfos(IData input) throws Exception{
        OneKeyStopMobileBean bean = (OneKeyStopMobileBean) BeanManager.createBean(OneKeyStopMobileBean.class);
        return bean.queryUUInfos(input);      
    }
    
    public IDataset queryPayLog(IData input) throws Exception{
        OneKeyStopMobileBean bean = (OneKeyStopMobileBean) BeanManager.createBean(OneKeyStopMobileBean.class);
        return bean.queryPayLog(input);      
    }
    
    public IDataset queryMonFeeInfos(IData input) throws Exception{
        OneKeyStopMobileBean bean = (OneKeyStopMobileBean) BeanManager.createBean(OneKeyStopMobileBean.class);
        return bean.queryMonFeeInfos(input);      
    }
    
    public IDataset queryCdrBil(IData input) throws Exception{
        OneKeyStopMobileBean bean = (OneKeyStopMobileBean) BeanManager.createBean(OneKeyStopMobileBean.class);
        return bean.queryCdrBil(input);      
    }
    
    public IDataset checkOpenDate(IData input) throws Exception{
        OneKeyStopMobileBean bean = (OneKeyStopMobileBean) BeanManager.createBean(OneKeyStopMobileBean.class);
        return bean.checkOpenDate(input);      
    }
}
