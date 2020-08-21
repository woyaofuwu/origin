
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class GiftsOccupyAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeOtherList = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
        int size = tradeOtherList.size();
        for (int i = 0; i < size; i++)
        {
            IData tradeOther = tradeOtherList.getData(i);
            if ("PRES".equals(tradeOther.getString("RSRV_VALUE_CODE")) && "C".equals(tradeOther.getString("RSRV_STR5")) && StringUtils.isNotBlank(tradeOther.getString("RSRV_STR20")))
            {
                ResCall.occupyGoods(mainTrade.getString("TRADE_EPARCHY_CODE"), tradeOther.getString("RSRV_STR20"), tradeOther.getString("RSRV_STR21"), tradeOther.getString("RSRV_STR19"), mainTrade.getString("TRADE_ID"));
            }
        }

    }

}
