
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserUsingBizSVC extends CSBizService
{

    public IDataset queryUserUsingData(IData data) throws Exception
    {
        QueryUserUsingBizBean qryBean = BeanManager.createBean(QueryUserUsingBizBean.class);
        return qryBean.QueryUserUsingBizInfo2(data);
    }

}
