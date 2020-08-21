
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trademgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeQuerySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset qryErrorMsg(IData input) throws Exception
    {
        return TradeQueryBean.qryErrorMsg(input);
    }

    public IDataset queryTradeInfo(IData input) throws Exception
    {
        return TradeQueryBean.queryTradeInfo(input);
    }

    public IDataset tradePfAgain(IData input) throws Exception
    {
        return TradeQueryBean.tradePfAgain(input);
    }
}
