package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;

/**
 * 物联网2个短信服务，在产品变更时，不允许更改。 
 * [99011000]物联网短信(标准)     [99011002]物联网短信(专用版)
 */
public class CheckWLWSMSAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        System.out.println("CheckWLWSMSActionxxxxxxxxxxxxxxxxxxxxxxxxx22 " + btd);

        List<SvcTradeData> svcTrades = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);//获取服务子台帐

        if (svcTrades != null && svcTrades.size() > 0) {
            for (int i = 0; i < svcTrades.size(); i++) {
                SvcTradeData svcdata = svcTrades.get(i);
                if (svcdata.getElementId().equals("99011000") || svcdata.getElementId().equals("99011002")) {
                    if (svcdata.getModifyTag().equals("2")) {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网短信服务属性不允许做变更!");
                    }
                }

            }
        }
    }
}
