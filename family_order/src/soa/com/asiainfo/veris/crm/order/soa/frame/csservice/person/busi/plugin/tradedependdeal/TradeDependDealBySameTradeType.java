
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plugin.tradedependdeal;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.plugin.IPlugin;

public class TradeDependDealBySameTradeType implements IPlugin
{

    @Override
    public void deal(List<BusiTradeData> btds, IData input) throws Exception
    {
        int btdSize = btds.size();

        if (btdSize > 1)
        {
            boolean limitTag = false;

            IDataset tradeLimitList = new DatasetList();
            tradeLimitList.clear();
            BusiTradeData mainBtd = btds.get(0);// 第一笔TRADE
            String mainTradeTypeCode = mainBtd.getTradeTypeCode();

            boolean flag = TradeCtrl.getCtrlBoolean(mainTradeTypeCode, TradeCtrl.CTRL_TYPE.SAME_TRADETYPE_DEPEND, false);
            if (flag)
            {
                for (int i = 1; i < btdSize; i++)// 从1开始取值
                {
                    BusiTradeData btd = btds.get(i);// 当前TRADE

                    String currTradeTypeCode = btd.getTradeTypeCode();

                    if (!mainTradeTypeCode.equals(currTradeTypeCode))
                    {
                        continue;
                    }

                    limitTag = true;
                    String limitTradeId = mainBtd.getTradeId();
                    String tradeId = btd.getTradeId();

                    IData tradeLimit = new DataMap();
                    tradeLimit.clear();

                    tradeLimit.put("TRADE_ID", tradeId);
                    tradeLimit.put("LIMIT_TRADE_ID", limitTradeId);
                    tradeLimit.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
                    tradeLimit.put("LIMIT_TYPE", "0");
                    tradeLimit.put("ROUTE_ID", btd.getRoute());
                    tradeLimit.put("STATE", "0");
                    tradeLimitList.add(tradeLimit);
                }

                if (limitTag && tradeLimitList.size() > 0)
                {
                    for (int j = 0; j < tradeLimitList.size(); j++)
                    {
                        Dao.insert("TF_B_TRADE_LIMIT", tradeLimitList.getData(j), Route.getJourDb(Route.CONN_CRM_CG));
                    }
                }
            }
        }
    }
}
