
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;

public class WidenetChangeProductTrade extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {

        btd.getMainTradeData().setSubscribeType("300");
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        super.createBusiTradeData(btd);
        appendTradeMainData(btd);

    }
}
