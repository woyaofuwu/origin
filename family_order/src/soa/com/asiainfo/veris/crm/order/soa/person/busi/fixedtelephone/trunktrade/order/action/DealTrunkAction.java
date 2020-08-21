
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.action;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class DealTrunkAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        String userId = mainTrade.getString("USER_ID");
        String userIdA = mainTrade.getString("RSRV_STR2");
        UserInfoQry.updateTrunkByUserId(userId, userIdA);

    }

}
