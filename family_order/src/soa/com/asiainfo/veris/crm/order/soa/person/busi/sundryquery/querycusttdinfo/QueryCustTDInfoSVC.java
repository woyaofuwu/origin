
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querycusttdinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryCustTDInfoSVC extends CSBizService
{
    public IDataset getCustTDTradeInfo(IData param) throws Exception
    {
        QueryCustTDInfoBean bean = (QueryCustTDInfoBean) BeanManager.createBean(QueryCustTDInfoBean.class);
        return bean.getCustTDTradeInfo(param, getPagination());
    }
}
