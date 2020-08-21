
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ChangePhonePreRegisterReginSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "802";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "802";
    }
    
    @Override
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {

    }

}
