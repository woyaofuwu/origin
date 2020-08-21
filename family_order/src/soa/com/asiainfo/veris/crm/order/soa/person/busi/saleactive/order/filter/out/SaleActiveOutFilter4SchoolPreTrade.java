
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.out;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class SaleActiveOutFilter4SchoolPreTrade implements IFilterOut
{

    @SuppressWarnings("unchecked")
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        IData param = new DataMap();

        param.putAll(input);
        param.put("TRADE_ID", input.getString("PRE_ID"));
        param.put("START_DATE", saleActiveReqData.getStartDate());
        param.put("END_DATE", saleActiveReqData.getEndDate());

        SaleActiveBean saleActiveBean = new SaleActiveBean();
        saleActiveBean.preTrade4SchoolActive(param);

        IData outData = new DataMap();

        outData.put("X_RESULTCODE", "0");
        outData.put("X_RESULTINFO", "OK");
        outData.put("OPER_NUMB", input.getString("OPER_NUMB"));
        outData.put("RECEIVE_OPER_NUMB", input.getString("OPER_NUMB"));
        outData.put("ORDER_CODE", input.getString("ORDER_CODE"));
        outData.put("BIZ_CATALOG", input.getString("BIZ_CATALOG"));
        outData.put("IS_VERIFY_SUCCESS", "1");

        return outData;
    }

}
