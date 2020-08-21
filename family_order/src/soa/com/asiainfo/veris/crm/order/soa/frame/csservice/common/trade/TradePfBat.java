
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public final class TradePfBat
{
    public static boolean isPfBat(IData order) throws Exception
    {
        // 订单类型
        String subscribeType = order.getString("SUBSCRIBE_TYPE");

        if (StringUtils.isNotBlank(subscribeType) && subscribeType.equals(BofConst.SUBSCRIBE_TYPE_BATCH_PF_FILE))
        {
            return true;
        }

        return false;
    }

    public static void pfBat(IData orderData, IDataset tradeAll) throws Exception
    {
        StringBuilder tradeDbSrc = new StringBuilder(1000);

        String routeId = "";
        int index = 0;

        for (int i = 0; i < tradeAll.size(); i++)
        {
            IData map = tradeAll.getData(i);

            routeId = map.getString("ROUTE_ID", "");

            if (StringUtils.isBlank(routeId))
            {
                continue;
            }

            // 是否已有
            index = tradeDbSrc.indexOf(routeId);

            if (index == -1)
            {
                tradeDbSrc.append(routeId).append(",");
            }
        }

        DataMap data = new DataMap();

        data.put("ORDER_ID", orderData.getString("ORDER_ID"));
        data.put("ACCEPT_MONTH", orderData.getString("ACCEPT_MONTH"));
        data.put("CANCEL_TAG", orderData.getString("CANCEL_TAG"));
        data.put("BATCH_ID", orderData.getString("BATCH_ID"));
        data.put("DEAL_STATE", "0");
        data.put("ROUTE_ID", BizRoute.getRouteId());
        data.put("CREATE_DATE", SysDateMgr.getSysTime());
        data.put("TRADE_DBSRCNAMES", tradeDbSrc.toString());

        Dao.insert("TF_B_BAT_ORDERPF", data, Route.CONN_CRM_CEN);
    }
}
