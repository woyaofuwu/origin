
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeSyncSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset sync(IData input) throws Exception
    {
        TradeSync.sync(input);

        return new DatasetList();
    }
}
