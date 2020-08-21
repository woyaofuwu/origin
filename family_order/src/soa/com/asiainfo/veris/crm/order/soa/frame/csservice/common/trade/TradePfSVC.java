
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizServiceAee;

public class TradePfSVC extends CSBizServiceAee
{
    private static final long serialVersionUID = 1L;

    public IDataset backPf(IData input) throws Exception
    {
        IDataset ids = TradePf.backPf(input);

        return ids;
    }

    public IDataset getPf(IData input) throws Exception
    {
        String orderId = input.getString("ORDER_ID");
        String acceptMonth = input.getString("ACCEPT_MONTH");
        String cancelTag = input.getString("CANCEL_TAG");

        IData id = TradePf.getPf(orderId, acceptMonth, cancelTag);

        IDataset ids = IDataUtil.idToIds(id);

        return ids;
    }

    public IDataset sendPf(IData input) throws Exception
    {
        String orderId = input.getString("ORDER_ID");
        String acceptMonth = input.getString("ACCEPT_MONTH");
        String cancelTag = input.getString("CANCEL_TAG");

        TradePf.sendPf(orderId, acceptMonth, cancelTag);

        return new DatasetList();
    }
}
