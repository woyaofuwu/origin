
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.simcardmissbill;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuerySimcardMissbillSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：SIM卡漏账查询 作者：GongGuang
     */
    public IDataset querySimcardMissbill(IData data) throws Exception
    {
        QuerySimcardMissbillBean bean = (QuerySimcardMissbillBean) BeanManager.createBean(QuerySimcardMissbillBean.class);
        return bean.querySimcardMissbill(data, getPagination());
    }

    public IDataset querySimcardType(IData data) throws Exception
    {
        QuerySimcardMissbillBean bean = (QuerySimcardMissbillBean) BeanManager.createBean(QuerySimcardMissbillBean.class);
        return bean.querySimcardType(data);
    }
}
