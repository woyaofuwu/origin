
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.action;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;

public class ChangeNumOccupyPhoneAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String seriNume = mainTrade.getString("SERIAL_NUMBER");
        String NewSeriNum = mainTrade.getString("RSRV_STR1");
        String userId = mainTrade.getString("USER_ID");
        String xGetModeString = "1";
        String resTypeCode = "N";
        if (StringUtils.isNotBlank(NewSeriNum))
        {
            PBossCall.resRealOccupy(NewSeriNum, "", userId);

            PBossCall.releasePhone(seriNume, "1");
        }
    }

}
