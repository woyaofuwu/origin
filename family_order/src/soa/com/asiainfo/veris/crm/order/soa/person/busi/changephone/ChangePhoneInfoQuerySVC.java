
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangePhoneInfoQuerySVC extends CSBizService
{

    public IDataset queryChangeCardInfo(IData input) throws Exception
    {

        ChangePhoneInfoQueryBean bean = (ChangePhoneInfoQueryBean) BeanManager.createBean(ChangePhoneInfoQueryBean.class);

        return bean.queryChangeCardInfo(input);
    }

    /**
     * 返回newIMSI给IBOSS
     */
    public IDataset queryNewImsiByTrade(IData input) throws Exception
    {

        ChangePhoneInfoQueryBean bean = (ChangePhoneInfoQueryBean) BeanManager.createBean(ChangePhoneInfoQueryBean.class);

        return bean.queryNewImsiByTrade(input);
    }
}
