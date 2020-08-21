package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.undo;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * 资源信息返销
 * 
 * @author
 */
public class UndoYDHKAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
       // 资源信息返销
       ResCall.changeSimCardCancelYD(mainTrade.getString("RSRV_STR4"));
    }
}



