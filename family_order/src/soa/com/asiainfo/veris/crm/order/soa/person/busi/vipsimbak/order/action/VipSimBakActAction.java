
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.action;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * @CREATED by gongp@2013-8-24
 */
public class VipSimBakActAction implements ITradeFinishAction
{
    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String resSimCardNo = mainTrade.getString("RSRV_STR3");
        if (StringUtils.isNotBlank(resSimCardNo))
        {
            // ResCall.resPossessForSim("3", mainTrade.getString("SERIAL_NUMBER"), resSimCardNo, "1",
            // mainTrade.getString("TRADE_ID"), mainTrade.getString("TRADE_TYPE_CODE"), "", "");
            ResCall.resPossessForSimAgent("3", mainTrade.getString("SERIAL_NUMBER"), resSimCardNo, "0", mainTrade.getString("TRADE_ID"), mainTrade.getString("USER_ID"), "0", "0", mainTrade.getString("PRODUCT_ID"), mainTrade
                    .getString("TRADE_TYPE_CODE"), "");

        }

    }

}
