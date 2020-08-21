
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.requestdata.WidenetProductRequestData;

public class NoPhoneWideChangeProdTrade extends ChangeProductTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        WidenetProductRequestData widenetProductRD = (WidenetProductRequestData) btd.getRD();
        
        btd.getMainTradeData().setSubscribeType("300");
        
        btd.getMainTradeData().setRsrvStr5(widenetProductRD.getYearDiscntRemainFee());
        btd.getMainTradeData().setRsrvStr6(widenetProductRD.getRemainFee());
        btd.getMainTradeData().setRsrvStr7(widenetProductRD.getAcctReainFee());
        btd.getMainTradeData().setRsrvStr8(widenetProductRD.getWideActivePayFee());
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        super.createBusiTradeData(btd);
        appendTradeMainData(btd);
        
    }
}
