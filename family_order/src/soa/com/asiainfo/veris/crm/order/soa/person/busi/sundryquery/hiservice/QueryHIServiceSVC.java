
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.hiservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryHIServiceSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：用于查询交换机信息 作者：GongGuang
     */
    public IDataset queryHIService(IData data) throws Exception
    {
        QueryHIServiceBean bean = (QueryHIServiceBean) BeanManager.createBean(QueryHIServiceBean.class);
        return bean.queryHIService(data, getPagination());
    }
}
