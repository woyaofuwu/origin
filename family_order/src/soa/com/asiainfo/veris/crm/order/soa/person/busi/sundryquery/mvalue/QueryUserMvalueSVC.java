
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.mvalue;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserMvalueSVC extends CSBizService
{
    public IData isPrint(IData input) throws Exception
    {
        String staffId = getVisit().getStaffId();
        QueryUserMvalueBean queryUserMvalueBean = (QueryUserMvalueBean) BeanManager.createBean(QueryUserMvalueBean.class);
        return queryUserMvalueBean.isPrint(staffId, "sys");
    }

    public IDataset queryCycleList(IData input) throws Exception
    {
        QueryUserMvalueBean queryUserMvalueBean = (QueryUserMvalueBean) BeanManager.createBean(QueryUserMvalueBean.class);
        return queryUserMvalueBean.queryCycleList();
    }

    public IDataset queryUserMvalue(IData input) throws Exception
    {
        QueryUserMvalueBean queryUserMvalueBean = (QueryUserMvalueBean) BeanManager.createBean(QueryUserMvalueBean.class);
        return queryUserMvalueBean.queryMvalue(input, getPagination());
    }
}
