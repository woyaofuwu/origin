
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.finish;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;

public class VipAssureOpenSendCreditFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String userId = mainTrade.getString("USER_ID");
        String remark = mainTrade.getString("REMARK");
        String openHours = mainTrade.getString("RSRV_STR3");
   
        // 大客户担保开机，需要调用信控流程，需要到现场后跟信控协商接口
        if ("492".equals(tradeTypeCode))
        {
            CreditCall.vipAssureOpen(mainTrade);
        }
    }
}
