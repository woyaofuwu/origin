
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npreturnvisit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NpReturnVisitSVC extends CSBizService
{
    public IDataset getNpOutInfos(IData param) throws Exception
    {
        NpReturnVisitBean bean = (NpReturnVisitBean) BeanManager.createBean(NpReturnVisitBean.class);
        return bean.getNpOutInfos(param);
    }

    public IDataset qryNpLogDetail(IData param) throws Exception
    {
        NpReturnVisitBean bean = (NpReturnVisitBean) BeanManager.createBean(NpReturnVisitBean.class);
        return bean.getNpSoasBySnUserId(param);
    }

    public IData submitNpInfo(IData param) throws Exception
    {
        NpReturnVisitBean bean = (NpReturnVisitBean) BeanManager.createBean(NpReturnVisitBean.class);
        return bean.submitNpInfo(param);
    }

}
