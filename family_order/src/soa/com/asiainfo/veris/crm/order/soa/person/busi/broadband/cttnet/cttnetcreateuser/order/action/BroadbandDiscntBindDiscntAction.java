
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.action;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;

public class BroadbandDiscntBindDiscntAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        int size = discntTradeDatas.size();
        for (int i = 0; i < size; i++)
        {
            DiscntTradeData discntTradeData = discntTradeDatas.get(i);
            if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
            {
                String endDate = discntTradeData.getEndDate();
                String startDate = discntTradeData.getStartDate();
                int day = SysDateMgr.dayInterval(startDate, endDate);
                discntTradeData.setRsrvNum1(String.valueOf(day));
            }

        }

    }

}
