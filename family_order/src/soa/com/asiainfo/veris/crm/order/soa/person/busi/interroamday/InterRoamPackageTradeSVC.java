package com.asiainfo.veris.crm.order.soa.person.busi.interroamday;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class InterRoamPackageTradeSVC extends CSBizService{
    
    private static final long serialVersionUID = 1L;
        
    public IDataset GiftRelationQuery(IData input) throws Exception{
    	InterRoamPackageTradeBean bean = (InterRoamPackageTradeBean) BeanManager.createBean(InterRoamPackageTradeBean.class);
        return bean.GiftRelationQuery(input);
    }
    public IDataset interRoamPackageQuery(IData input) throws Exception{
    	InterRoamPackageTradeBean bean = (InterRoamPackageTradeBean) BeanManager.createBean(InterRoamPackageTradeBean.class);
        return bean.interRoamPackageQuery(input);
    }
}