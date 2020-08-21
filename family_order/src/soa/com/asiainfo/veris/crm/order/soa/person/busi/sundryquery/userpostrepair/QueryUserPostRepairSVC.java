
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userpostrepair;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserPostRepairSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：用户邮寄补寄信息 作者：GongGuang
     */
    public IDataset queryUserPostRepair(IData data) throws Exception
    {
        QueryUserPostRepairBean bean = (QueryUserPostRepairBean) BeanManager.createBean(QueryUserPostRepairBean.class);
        return bean.queryUserPostRepair(data, getPagination());
    }

    public IDataset submitUserRepairPost(IData data) throws Exception
    {
        QueryUserPostRepairBean bean = (QueryUserPostRepairBean) BeanManager.createBean(QueryUserPostRepairBean.class);
        return bean.submitUserRepairPost(data, getPagination());
    }

}
