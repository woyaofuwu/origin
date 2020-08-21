
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbindremove;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ECardGprsBindRemoveSVC extends CSBizService
{
    public IDataset queryCrmInfos(IData input) throws Exception
    {
        ECardGprsBindRemoveBean bean = (ECardGprsBindRemoveBean) BeanManager.createBean(ECardGprsBindRemoveBean.class);
        return bean.queryCrmInfos(input);
    }

    public IDataset queryRelationUU(IData input) throws Exception
    {
        ECardGprsBindRemoveBean bean = (ECardGprsBindRemoveBean) BeanManager.createBean(ECardGprsBindRemoveBean.class);
        return bean.checkERelationInfo(input);

    }

}
