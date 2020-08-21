
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.veml;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public class VemlIbossOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        input.put("ID_TYPE", "01");
        input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));
        input.put("OPR_NUMB", input.getString("OPR_NUMB"));
        input.put("BIZ_ORDER_RESULT", input.getString("X_RESULTCODE"));
        return input;
    }

}
