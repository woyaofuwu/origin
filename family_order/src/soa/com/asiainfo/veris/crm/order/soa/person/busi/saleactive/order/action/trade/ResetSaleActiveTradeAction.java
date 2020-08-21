
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class ResetSaleActiveTradeAction implements ITradeAction
{

    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveReqData request = (SaleActiveReqData) btd.getRD();

        List<SaleActiveTradeData> saleActives = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);

        for (SaleActiveTradeData saleActive : saleActives)
        {
            boolean isQyyxActive = SaleActiveUtil.isQyyx(request.getCampnType());

            if (isQyyxActive)
            {
                saleActive.setRsrvDate1(request.getOnNetStartDate());
                saleActive.setRsrvDate2(request.getOnNetEndDate());
            }
            else if (StringUtils.isNotBlank(request.getChargeId()))
            {
                saleActive.setRsrvStr2(request.getChargeId());
            }

            String callType = request.getCallType();

            if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(callType))
            {
                saleActive.setRsrvTag3("T");
            }
        }
    }

}
