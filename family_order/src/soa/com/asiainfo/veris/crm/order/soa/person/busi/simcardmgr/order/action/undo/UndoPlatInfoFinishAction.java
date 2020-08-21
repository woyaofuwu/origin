
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.undo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

/**
 * 手机平台处理返销
 */
public class UndoPlatInfoFinishAction implements ITradeFinishAction
{
    protected static Logger log = Logger.getLogger(UndoPlatInfoFinishAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
        String rsrvStr5 = mainTrade.getString("RSRV_STR5", "");

        if ("1".equals(rsrvStr5))
        {
            IBossCall.changCard2NotifyIBOSS(mainTrade.getString("SERIAL_NUMBER"), mainTrade.getString("RSRV_STR7"), mainTrade.getString("RSRV_STR6"), mainTrade.getString("IN_MODE_CODE"), mainTrade.getString("TRADE_TYPE_CODE"), mainTrade
                    .getString("TRADE_CITY_CODE"), mainTrade.getString("TRADE_DEPART_ID"), mainTrade.getString("UPDATE_STAFF_ID"), CSBizBean.getVisit().getProvinceCode(), "01", mainTrade.getString("SERIAL_NUMBER"));
        }
    }
}
