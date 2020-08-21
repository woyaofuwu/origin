
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.SynMebWordOrder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SynMebWordOrderSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public final IDataset dealMebWordOrder(IData map) throws Exception
    {
        // 1- 定义返回对象
        IDataset dealResultSet = new DatasetList();

        // 2- 获取返回结果
        SynMebWordOrder sw = new SynMebWordOrder();
        dealResultSet = sw.dealMebWordOrder(map);

        // 3- 返回结果
        return dealResultSet;
    }

}
