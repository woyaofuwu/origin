
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangePhonePreInfoQuerySVC extends CSBizService
{

    public IDataset queryChangeCardInfo(IData input) throws Exception
    {

        ChangePhonePreInfoQueryBean bean = (ChangePhonePreInfoQueryBean) BeanManager.createBean(ChangePhonePreInfoQueryBean.class);

        return bean.queryChangeCardInfo(input);

    }

}
