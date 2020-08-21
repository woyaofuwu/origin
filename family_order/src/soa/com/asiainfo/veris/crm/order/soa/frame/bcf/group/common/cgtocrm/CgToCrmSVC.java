
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.cgtocrm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CgToCrmSVC extends CSBizService
{

    public void execCgToCrm(IData data) throws Exception
    {
        CgToCrmBean.execCgToCrm(data);
    }

    // 根据TRADE_ID查找SYNC_SEQUENCE和SYNC_DAY
    public IDataset execSYNCByTradeId(IData data) throws Exception
    {
        // data-->tradeId
        IDataset dataset = CgToCrmBean.execSYNCByTradeId(data, getPagination());
        return dataset;
    }

    public IDataset queryInfoByTradeId(IData data) throws Exception
    {
        IDataset dataset = CgToCrmBean.queryInfoByTradeId(data, getPagination());
        return dataset;
    }

    // 查询TI_B_USER_CRGTOCRM数据库中的TRADE_ID
    public IDataset queryTradeId(IData data) throws Exception
    {
        IDataset dataset = CgToCrmBean.queryTradeId(getPagination());
        return dataset;
    }
}
