
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ScoreExchMobilSelfRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "339";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "339";
    }

    @Override
    public final void setTrans(IData input)
    {
        IDataset lstPackage = new DatasetList(input.getString("STACK_PACKAGE"));
        IDataset lstItem = new DatasetList(lstPackage.getData(0).getString("ITEM_INFO"));
        input.put("SERIAL_NUMBER", lstItem.getData(0).getString("B_MOBILE", ""));
    }

}
