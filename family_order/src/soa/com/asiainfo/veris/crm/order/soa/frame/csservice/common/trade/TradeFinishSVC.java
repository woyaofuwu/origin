
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizServiceAee;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.TradeLogUtil;

public class TradeFinishSVC extends CSBizServiceAee
{
    final static Logger logger = Logger.getLogger(TradeFinishSVC.class);

    private IDataset delKeys = null;

    private static final long serialVersionUID = 1L;

    private void delUCAKey() throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("delUCAkey start");
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
            logger.debug("delUCAkey end");
        }
    }

    public void destroy(IDataInput input, IDataOutput output) throws Exception
    {
        super.destroy(input, output);

        // uca缓存清空（必须放到最后一步）
        delUCAKey();
    }

    public IDataset finish(IData input) throws Exception
    {
        delKeys = null;

        String tradeId = input.getString("TRADE_ID");
        String acceptMonth = input.getString("ACCEPT_MONTH");
        String canceltag = input.getString("CANCEL_TAG");
        String userId = input.getString("USER_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String routeId = input.getString(Route.ROUTE_EPARCHY_CODE);
        
        TradeLogUtil.log(tradeId, "tradeStart", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // 得到缓存key
        delKeys = TradeFinish.getUcakey(tradeId, acceptMonth, userId, tradeTypeCode, routeId);

        // 执行完工
        long start = System.currentTimeMillis();
		try {
			TradeFinish.finish(tradeId, acceptMonth, canceltag, routeId);
		} catch (Exception e) {
			try {
				IDataset tradeset = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, canceltag);
				if (IDataUtil.isNotEmpty(tradeset)) {
					IData mainTrade = tradeset.getData(0);
					TradeFinish.notificationPfMonOrderAction(mainTrade, BofConst.PF_COMPLAIN_VISUALIZATION_FINISH_FAIL);
				}
			} catch (Exception e1) {
			}
			throw e;
		}
		
        TradeLogUtil.log(tradeId, "tradeEnd", System.currentTimeMillis() - start);
        return new DatasetList();
    }
}
