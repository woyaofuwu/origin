
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.userconn.UserConnQry;

public class DeleteUserConnection implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset widenetTradeInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
        if (IDataUtil.isNotEmpty(widenetTradeInfos))
        {
            IData wideNetTradeInfo = widenetTradeInfos.getData(0);
            String userIdB = wideNetTradeInfo.getString("RSRV_STR5");
            IDataset userConnDataset = UserConnQry.getConnInfosByIdA(userId, "BK");
            if (IDataUtil.isNotEmpty(userConnDataset) && StringUtils.isNotBlank(userIdB) && StringUtils.equals(userIdB, userConnDataset.getData(0).getString("USER_ID_B")))
            {
                UserConnQry.deleteUserConnection(userId, userIdB, "BK");
            }
        }
    }

}
