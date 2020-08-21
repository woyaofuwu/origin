
package com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.order.action.trade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.order.requestdata.SaleActiveTransReqData;

public class TransAcctActiveTradeActiveon implements ITradeAction
{

    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveTransReqData requestData = (SaleActiveTransReqData) btd.getRD();

        String targetUserId = UcaDataFactory.getNormalUca(requestData.getTargetSn(), false, false).getUserId();

        UcaData uca = btd.getRD().getUca();

        String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, requestData.getRelationTradeId());

        if (StringUtils.isBlank(actionCode))
        {
            return;
        }

        String outTradeId = requestData.getRelationTradeId();
        String inTradeId = requestData.getTradeId();

        AcctCall.transDiscntByTradeId(uca.getUserId(), targetUserId, outTradeId, inTradeId, actionCode);
    }

}
