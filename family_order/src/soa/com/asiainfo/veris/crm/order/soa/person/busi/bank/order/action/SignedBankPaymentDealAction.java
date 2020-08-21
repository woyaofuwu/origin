
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.action;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.requestdata.NewSvcRecomdInfoRequestData;

public class SignedBankPaymentDealAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {

        List<MainTradeData> mainList = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
        NewSvcRecomdInfoRequestData reqData = (NewSvcRecomdInfoRequestData) btd.getRD();

        mainList.get(0).setRemark(reqData.getRemark());// 备注

    }

}
