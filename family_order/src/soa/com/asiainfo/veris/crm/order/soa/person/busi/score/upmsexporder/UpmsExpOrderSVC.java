
package com.asiainfo.veris.crm.order.soa.person.busi.score.upmsexporder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UpmsExpOrderSVC extends CSBizService
{
    public IDataset queryExpOrder(IData input) throws Exception
    {
        UpmsExpOrderBean bean = (UpmsExpOrderBean) BeanManager.createBean(UpmsExpOrderBean.class);
        return bean.queryExpOrder(input, this.getPagination());
    }
}
