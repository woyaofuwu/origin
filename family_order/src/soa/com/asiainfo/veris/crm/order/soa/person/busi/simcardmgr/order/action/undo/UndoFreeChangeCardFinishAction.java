
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.undo;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherservQry;

/**
 * 免费换卡完工插TF_F_USER_OTHERSERV 表，回收
 * 
 * @author
 */
public class UndoFreeChangeCardFinishAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        // 免费换卡回收
        UserOtherservQry.delUserOtherservByRsrvStr6(mainTrade.getString("USER_ID"), mainTrade.getString("TRADE_ID"));
    }

}
