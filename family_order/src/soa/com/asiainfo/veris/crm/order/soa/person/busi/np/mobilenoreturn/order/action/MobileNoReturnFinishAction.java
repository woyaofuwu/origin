
package com.asiainfo.veris.crm.order.soa.person.busi.np.mobilenoreturn.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class MobileNoReturnFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String rsrv_str8 = mainTrade.getString("RSRV_STR8");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String rsrvStr1 = mainTrade.getString("RSRV_STR1");
        if ("4".equals(rsrv_str8))
        {
            // 修改号码状态
            // 修改卡状态
            ResCall.modifyNpSimInfo(rsrvStr1, "10", serialNumber, tradeId, userId);
            ResCall.modifyNpMphoneInfo(rsrvStr1, "10", serialNumber);
            IData param = new DataMap();
            param.put("USER_ID", mainTrade.getString("USER_ID"));
            param.put("NP_TAG", "5");
            Dao.executeUpdateByCodeCode("TF_F_USER_NP", "UPD_DESTROYNP_BY_ID", param);
        }

    }

}
