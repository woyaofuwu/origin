
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userimei;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserImeiSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：用于查询用户IMEI信息 作者：GongGuang
     */
    public IDataset queryUserImei(IData data) throws Exception
    {
        QueryUserImeiBean bean = (QueryUserImeiBean) BeanManager.createBean(QueryUserImeiBean.class);
        return bean.queryUserImei(data, getPagination());
    }
}
