
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CustVipInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset getCustVipInfo(IData input) throws Exception
    {
        IData data = CustVipInfoQry.getCustVipInfo(input);

        IDataset dataset = new DatasetList();
        if (!IDataUtil.isEmpty(data))
        {
            dataset.add(data);
        }

        return dataset;
    }
}
