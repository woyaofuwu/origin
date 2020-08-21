
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.undofinish;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class UndoSaleActiveAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeId = mainTrade.getString("TRADE_ID");
        if (serialNumber.startsWith("KD_"))
        {
            serialNumber = serialNumber.substring(3);
        }
        UserSaleActiveInfoQry.updateBook2ValidSaleActiveByAcceptTradeId(tradeId, serialNumber);
    }

}
