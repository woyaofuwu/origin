
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.uniontrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUnionTransSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：异网转接查询 作者：GongGuang
     */
    public IDataset queryUnionTrans(IData data) throws Exception
    {
        QueryUnionTransBean bean = (QueryUnionTransBean) BeanManager.createBean(QueryUnionTransBean.class);
        return bean.queryUnionTrans(data, getPagination());
    }
}
