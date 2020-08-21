
package com.asiainfo.veris.crm.order.soa.group.demo;

import org.apache.log4j.Logger;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeFinish;

public class TestDelUCAKeySVC extends CSBizService
{

    private static final long serialVersionUID = -4621598683108458088L;

    final static Logger logger = Logger.getLogger(TestDelUCAKeySVC.class);

    private IDataset delKeys = null;

    public IDataset delUCAKey(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");
        String acceptMonth = input.getString("ACCEPT_MONTH");
        String userId = input.getString("USER_ID");
        String routeId = input.getString(Route.ROUTE_EPARCHY_CODE);
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        // 得到缓存key
        delKeys = TradeFinish.getUcakey(tradeId, acceptMonth, userId, tradeTypeCode, routeId);

        delUCAKey();

        return null;
    }

    private void delUCAKey() throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("TestDelUCAKeySVC delUCAkey start");
        }

        if (IDataUtil.isEmpty(delKeys))
        {
            return;
        }

        IData map = null;
        String cacheKey = "";
        boolean delOK = false;

        for (int row = 0, size = delKeys.size(); row < size; row++)
        {
            map = delKeys.getData(row);

            cacheKey = map.getString("ID_KEY");

            // 在共享缓存中删除Key所对应的Value
            delOK = SharedCache.delete(cacheKey);

            if (logger.isDebugEnabled())
            {
                logger.debug("delUCAkey=[" + cacheKey + "] delOK=[" + delOK + "]");
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("TestDelUCAKeySVC delUCAkey end");
        }
    }
}
