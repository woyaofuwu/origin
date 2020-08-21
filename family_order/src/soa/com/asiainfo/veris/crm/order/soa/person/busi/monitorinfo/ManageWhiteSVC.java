
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ManageWhiteSVC extends CSBizService
{

    public IDataset delWhiteUser(IData data) throws Exception
    {
        ManageWhiteBean bean = BeanManager.createBean(ManageWhiteBean.class);
        return bean.delWhiteUser(data);
    }

    public IDataset loadChildInfo(IData data) throws Exception
    {
        ManageWhiteBean bean = BeanManager.createBean(ManageWhiteBean.class);
        return bean.loadChildInfo(data);
    }

    public IDataset onTradeSubmit(IData data) throws Exception
    {
        ManageWhiteBean bean = BeanManager.createBean(ManageWhiteBean.class);
        return bean.onTradeSubmit(data);
    }
}
