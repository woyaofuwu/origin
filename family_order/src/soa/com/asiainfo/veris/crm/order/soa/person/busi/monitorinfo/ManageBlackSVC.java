
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ManageBlackSVC extends CSBizService
{

    public IDataset delBlackUser(IData data) throws Exception
    {
        ManageBlackBean bean = BeanManager.createBean(ManageBlackBean.class);
        return bean.delBlackUser(data);
    }

    public IDataset loadChildInfo(IData data) throws Exception
    {
        ManageBlackBean bean = BeanManager.createBean(ManageBlackBean.class);
        return bean.loadChildInfo(data);
    }

    public IDataset onTradeSubmit(IData data) throws Exception
    {
        ManageBlackBean bean = BeanManager.createBean(ManageBlackBean.class);
        return bean.onTradeSubmit(data);
    }
}
