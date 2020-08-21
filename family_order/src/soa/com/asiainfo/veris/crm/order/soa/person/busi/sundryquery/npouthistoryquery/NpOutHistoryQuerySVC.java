
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npouthistoryquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NpOutHistoryQuerySVC extends CSBizService
{
    public IDataset getOutNpInfos(IData param) throws Exception
    {
        NpOutHistoryQueryBean bean = (NpOutHistoryQueryBean) BeanManager.createBean(NpOutHistoryQueryBean.class);
        return bean.getOutNpInfos(param);
    }
}
