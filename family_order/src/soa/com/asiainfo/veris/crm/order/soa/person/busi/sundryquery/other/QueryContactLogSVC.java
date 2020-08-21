
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryContactLogSVC extends CSBizService
{

    public IDataset queryContactLogs(IData input) throws Exception
    {
        QueryContactLogBean contactLogBean = (QueryContactLogBean) BeanManager.createBean(QueryContactLogBean.class);
        return contactLogBean.queryContactLog(input, getPagination());
    }

}
