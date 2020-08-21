
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order.action;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;

public class OccupyPhoneAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        // 号码占用
        String serial_number = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String psptId = "";
        PBossCall.resRealOccupy(serial_number, psptId, userId);
        String rsrvstr3 = mainTrade.getString("RSRV_STR3");
        String rsrvstr4 = mainTrade.getString("RSRV_STR4");
        // 座机占用
        if (StringUtils.isNotBlank(rsrvstr3))
        {
            String resNo = rsrvstr3;
            String resKindCode = "01";
            PBossCall.resMaterialOccupyGH(resNo, userId, resKindCode);
        }
        // 计费器占用
        if (StringUtils.isNotBlank(rsrvstr4))
        {
            String resNo = rsrvstr4;
            String resKindCode = "04";
            PBossCall.resMaterialOccupyGH(resNo, userId, resKindCode);
        }
    }

}
