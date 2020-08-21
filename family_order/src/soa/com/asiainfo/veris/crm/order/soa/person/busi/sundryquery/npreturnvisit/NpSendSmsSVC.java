
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npreturnvisit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NpSendSmsSVC extends CSBizService
{
    public IDataset getNpOutInfos(IData param) throws Exception
    {
        NpSendSmsBean bean = (NpSendSmsBean) BeanManager.createBean(NpSendSmsBean.class);
        return bean.getNpOutInfos(param, getPagination());
    }

    public IData upDateNpOutInfo(IData param) throws Exception
    {
        NpSendSmsBean bean = (NpSendSmsBean) BeanManager.createBean(NpSendSmsBean.class);
        return bean.upDateNpOutInfo(param);
    }
}
