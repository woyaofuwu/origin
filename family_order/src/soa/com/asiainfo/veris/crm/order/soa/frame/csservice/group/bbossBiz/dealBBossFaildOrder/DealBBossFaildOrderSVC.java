
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.dealBBossFaildOrder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DealBBossFaildOrderSVC extends CSBizService
{
    private static final long serialVersionUID = -2299741985122481704L;

    public final IDataset modifyTradeError(IData map) throws Exception
    {
        // 1- 定义返回对象
        IDataset dealResultSet = new DatasetList();

        // 2- 获取返回结果
        dealResultSet = DealBBossFaildOrder.modifyTradeError(map);

        // 3- 返回结果
        return dealResultSet;
    }
}
