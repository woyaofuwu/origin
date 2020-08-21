package com.asiainfo.veris.crm.order.soa.person.busi.smsboomprotection;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SmsBoomProtectionSVC  extends CSBizService {

    public void delProtectInfo(IData input) throws Exception
    {	
    	SmsBoomProtectionBean deleteProtectBean = BeanManager.createBean(SmsBoomProtectionBean.class);
        deleteProtectBean.deleteProtectInfo(input);
        
    }
    
    public IDataset qryProtectinfoBySerialNum(IData input) throws Exception
    {	
    	SmsBoomProtectionBean qryProtectinfoBean = BeanManager.createBean(SmsBoomProtectionBean.class);
        return qryProtectinfoBean.qryProtectinfoBySerialNum(input,getPagination());
    }
    
    public IDataset qryProtectinfo(IData input) throws Exception
    {	
    	SmsBoomProtectionBean qryProtectinfoBean = BeanManager.createBean(SmsBoomProtectionBean.class);
        return qryProtectinfoBean.qryProtectinfo(input,getPagination());
    }
    
    public IDataset qryProtectinfoByAccessNum(IData input) throws Exception
    {	
    	SmsBoomProtectionBean qryProtectinfoBean = BeanManager.createBean(SmsBoomProtectionBean.class);
        return qryProtectinfoBean.qryProtectinfoByAccessNum(input,getPagination());
    }
    //REQ201904080006关于开发短信炸弹及互联网中心密码重置接口的需求 wuhao5
    public IData qrySMSBombStatRecInf(IData input) throws Exception
    {	
    	SmsBoomProtectionBean qryProtectinfoBean = BeanManager.createBean(SmsBoomProtectionBean.class);
        return qryProtectinfoBean.qrySMSBombStatRecInf(input);
    }
    
    public  void updateProtectInfo(IData input) throws Exception
    {	
    	SmsBoomProtectionBean updateProtectInfoBean = BeanManager.createBean(SmsBoomProtectionBean.class);
        updateProtectInfoBean.updateProtectInfo(input);
    }
    

    public  void insertdateProtectInfo(IData input) throws Exception
    {	
    	SmsBoomProtectionBean insertProtectInfoBean = BeanManager.createBean(SmsBoomProtectionBean.class);
         insertProtectInfoBean.insertProtectInfo(input);
    }

    public  IData syncProtectedSMSBombList(IData input) throws Exception
    {	
    	SmsBoomProtectionBean synSmsProtectInfoBean = BeanManager.createBean(SmsBoomProtectionBean.class);
    	return synSmsProtectInfoBean.syncProtectedSMSBombList(input);
    }
}
