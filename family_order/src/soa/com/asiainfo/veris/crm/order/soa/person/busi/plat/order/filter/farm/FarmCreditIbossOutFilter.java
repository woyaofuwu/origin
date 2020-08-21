
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.farm;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public class FarmCreditIbossOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        input.put("ID_TYPE", input.getString("ID_TYPE"));
        input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));
        input.put("AREA_INFO", "41");// 青海：50；天津：21；湖南：39；海南：41；云南：47；陕西：48；新疆：52
        input.put("TRANS_ID", input.getString("TRANS_ID", input.getString("INTF_TRADE_ID")));

        return input;
    }

}
