
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.deratefee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryDerateFeeSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 查询业务区
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryArea(IData param) throws Exception
    {
        QueryDerateFeeBean bean = BeanManager.createBean(QueryDerateFeeBean.class);
        return bean.queryArea();

    }

    /**
     * 功能：减免费用查询 作者：GongGuang
     */
    public IDataset queryDerateFee(IData data) throws Exception
    {
        QueryDerateFeeBean bean = (QueryDerateFeeBean) BeanManager.createBean(QueryDerateFeeBean.class);
        return bean.queryDerateFee(data, getPagination());
    }

    /**
     * 功能：查询tradeTypeList 作者：GongGuang
     */
    public IDataset queryTradeTypeList(IData data) throws Exception
    {
        QueryDerateFeeBean bean = (QueryDerateFeeBean) BeanManager.createBean(QueryDerateFeeBean.class);
        return bean.queryTradeTypeList(data, getPagination());
    }
}
