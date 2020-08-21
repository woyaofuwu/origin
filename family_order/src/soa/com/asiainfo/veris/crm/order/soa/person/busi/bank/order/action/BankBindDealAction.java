
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public class BankBindDealAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {

    	String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String tradeTypeCode = btd.getTradeTypeCode();
        String eparchyCode = btd.getRD().getUca().getUser().getEparchyCode();

        IData params = new DataMap();
        params.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        params.put("USER_ID", userId);
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("TRADE_TYPE_CODE", tradeTypeCode);
        params.put("ACCEPT_TIME", btd.getRD().getAcceptTime());
        params.put("RSRV_STR1", btd.getMainTradeData().getRsrvStr1());
        params.put("RSRV_STR3", btd.getMainTradeData().getRsrvStr3());
        IDataset bankResults = CSAppCall.call("SS.BankIntfConnectSVC.bankBind", params);
        if (IDataUtil.isNotEmpty(bankResults))
        {
            IData data = bankResults.getData(0);
        }
    }
}
