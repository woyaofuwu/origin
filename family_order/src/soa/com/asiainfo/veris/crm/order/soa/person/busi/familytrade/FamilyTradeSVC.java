
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FamilyTradeSVC extends CSBizService
{

    public void checkAddMeb(IData data) throws Exception
    {
        FamilyTradeBean bean = BeanManager.createBean(FamilyTradeBean.class);
        bean.checkAddMeb(data);
    }

    /**
     * 亲情业务接口服务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData createFamilyTrade(IData data) throws Exception
    {
        FamilyTradeBean bean = BeanManager.createBean(FamilyTradeBean.class);
        return bean.createFamilyTrade(data);
    }

    public IDataset queryFamilyMebList(IData data) throws Exception
    {
        FamilyTradeBean bean = BeanManager.createBean(FamilyTradeBean.class);
        return bean.queryFamilyMebList(data);
    }
}
