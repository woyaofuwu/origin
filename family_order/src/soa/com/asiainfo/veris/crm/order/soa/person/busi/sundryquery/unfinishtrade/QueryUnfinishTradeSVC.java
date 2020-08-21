
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.unfinishtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUnfinishTradeSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：未完工工单查询 作者：GongGuang
     */
    public IDataset queryUnfinishTrade(IData data) throws Exception
    {
        QueryUnfinishTradeBean bean = (QueryUnfinishTradeBean) BeanManager.createBean(QueryUnfinishTradeBean.class);
        return bean.queryUnfinishTrade(data, getPagination());
    }
    
    /**
     * 功能：未完工工单台账轨迹查询 作者：chenwei6
     */
    public IDataset queryUnfinishTradeTrace(IData data) throws Exception
    {
        QueryUnfinishTradeBean bean = (QueryUnfinishTradeBean) BeanManager.createBean(QueryUnfinishTradeBean.class);
        return bean.queryUnfinishTradeTrace(data);
    }
    
    /**
     * 功能：未完工工单服开轨迹查询 作者：chenwei6
     */
    public IDataset queryUnfinishPFTrace(IData data) throws Exception
    {
        QueryUnfinishTradeBean bean = (QueryUnfinishTradeBean) BeanManager.createBean(QueryUnfinishTradeBean.class);
        return bean.queryUnfinishPFTrace(data);
    }
}
