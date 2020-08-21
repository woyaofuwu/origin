
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeBackSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset backByProc(IData input) throws Exception
    {
        TradeBack.back(input);

        return new DatasetList();
    }
}
