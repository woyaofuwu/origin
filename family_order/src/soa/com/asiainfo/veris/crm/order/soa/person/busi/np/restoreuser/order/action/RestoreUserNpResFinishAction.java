
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

public class RestoreUserNpResFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");

        IDataset ids = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(tradeId, "0");
        if (IDataUtil.isNotEmpty(ids))
        {
            for (int i = 0, len = ids.size(); i < len; i++)
            {
                IData data = ids.getData(i);
                if ("1".equals(data.getString("RES_TYPE_CODE")))
                {// SIM卡资源
                    // 废该手机号码原相关的sim卡
                    ResCall.modifyNpSimInfo(data.getString("RES_CODE"), "3", serialNumber, tradeId, userId);
                }
                else if ("0".equals(data.getString("RES_TYPE_CODE")))
                {// 号码资源
                    ResCall.modifyNpMphoneInfo(data.getString("RSRV_STR4"), "3", data.getString("RES_CODE"));
                }
            }
        }

    }

}
