
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryvipexchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryVipExchangeSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset queryVipExchange(IData data) throws Exception
    {
        QueryVipExchangeBean bean = (QueryVipExchangeBean) BeanManager.createBean(QueryVipExchangeBean.class);
        return bean.queryVipExchange(data, getPagination());
    }
}
