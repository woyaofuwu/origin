
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npartificialcheck;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NpArtificialCheckSVC extends CSBizService
{
    public IData checkNotPass(IData param) throws Exception
    {
        NpArtificialCheckBean bean = (NpArtificialCheckBean) BeanManager.createBean(NpArtificialCheckBean.class);
        return bean.checkNotPass(param);
    }

    public IData checkPass(IData param) throws Exception
    {
        NpArtificialCheckBean bean = (NpArtificialCheckBean) BeanManager.createBean(NpArtificialCheckBean.class);
        return bean.checkPass(param);
    }

    public IDataset queryTradeNpInfos(IData param) throws Exception
    {
        NpArtificialCheckBean bean = (NpArtificialCheckBean) BeanManager.createBean(NpArtificialCheckBean.class);
        return bean.queryTradeNpInfos(param, getPagination());
    }
}
