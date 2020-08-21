
package com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen.order.requestdata.ActiveSaleCardOpenReqData;

;

/**
 * 手工买断激活
 * 
 * @author sunxin
 */
public class ActiveSaleCardOpenTrade extends BaseTrade implements ITrade
{

    /**
     * 处理主台账的数据
     * 
     * @param reqData
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        ActiveSaleCardOpenReqData activeSaleCardOpenRD = (ActiveSaleCardOpenReqData) btd.getRD();

        btd.getMainTradeData().setRemark("买断开户手工激活");
    }

    // @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        appendTradeMainData(btd);
    }

}
