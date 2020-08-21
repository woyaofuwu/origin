
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.integralacctref.order.action;

import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class IntegralAcctRefAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTrade = btd.getMainTradeData();
        String endDate = mainTrade.getRsrvStr1();
        String refAcctId = mainTrade.getRsrvStr2();
        String oldAcctId = mainTrade.getRsrvStr3();
        String tradeId = btd.getTradeId();
        String userId = btd.getRD().getUca().getUserId();

        AcctCall.integralAcctRef(userId, oldAcctId, refAcctId, tradeId, endDate);
    }

}
