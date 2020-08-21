
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plugin.tradedependdeal;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.plugin.IPlugin;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

public class TradeDependDealByTradeType implements IPlugin
{
    public void deal(List<BusiTradeData> btds, IData input) throws Exception
    {
        int btdSize = btds.size();
        if (btdSize > 1)
        {
            IDataset tradeLimitList = new DatasetList();
            for (int i = 0; i < btdSize; i++)
            {
                BusiTradeData btd = btds.get(i);
                String tradeTypeCode = btd.getTradeTypeCode();
                String tradeId = btd.getTradeId();
                IDataset tradeLimitParams = BofQuery.queryDependTradeType(tradeTypeCode, CSBizBean.getTradeEparchyCode());
                int paramSize = tradeLimitParams.size();
                for (int j = 0; j < paramSize; j++)
                {
                    IData tradeLimitParam = tradeLimitParams.getData(j);
                    String limitTradeType = tradeLimitParam.getString("LIMIT_TRADE_TYPE_CODE");
                    for (int k = 0; k < btdSize; k++)
                    {
                        IData tradeLimit = new DataMap();
                        BusiTradeData compareBtd = btds.get(k);
                        String compareTradeTypeCode = compareBtd.getTradeTypeCode();
                        String compareTradeId = compareBtd.getTradeId();
                        if (tradeId.equals(compareTradeId))
                        {
                            // 过滤本身
                            continue;
                        }
                        if (limitTradeType.equals(compareTradeTypeCode))
                        {
                            tradeLimit.put("TRADE_ID", tradeId);
                            tradeLimit.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
                            tradeLimit.put("LIMIT_TRADE_ID", compareTradeId);
                            tradeLimit.put("LIMIT_TYPE", "0");
                            tradeLimit.put("ROUTE_ID", btd.getRoute());
                            tradeLimit.put("STATE", "0");
                            tradeLimitList.add(tradeLimit);
                        }
                    }
                }
            }
            Dao.insert("TF_B_TRADE_LIMIT", tradeLimitList, Route.getJourDb(Route.CONN_CRM_CG));
        }
    }

}
