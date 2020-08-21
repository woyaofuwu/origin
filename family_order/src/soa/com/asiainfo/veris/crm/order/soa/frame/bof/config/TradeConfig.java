
package com.asiainfo.veris.crm.order.soa.frame.bof.config;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.proxy.TradeProxy;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

/**
 * @author Administrator
 */
public class TradeConfig
{
    private static IData tradesMap = new DataMap();

    private static Logger log = Logger.getLogger(TradeConfig.class);

    static
    {
        try
        {
            IDataset trades = BofQuery.getAllTrade();
            int size = trades.size();
            // 加载到缓存里
            for (int i = 0; i < size; i++)
            {
                try
                {
                    String key = null;
                    IData trade = trades.getData(i);
                    String configTradeTypeCode = trade.getString("TRADE_TYPE_CODE");
                    String className = trade.getString("CLASS_NAME").trim();

                    key = configTradeTypeCode;
                    ITrade tradeBean = (ITrade) Class.forName(className).newInstance();
                    TradeProxy factory = new TradeProxy(tradeBean);
                    tradesMap.put(key, Proxy.newProxyInstance(tradeBean.getClass().getClassLoader(), tradeBean.getClass().getInterfaces(), factory));
                }
                catch (Exception e)
                {
                    log.error(e);
                }
            }
        }
        catch (Exception e)
        {
            log.error(e);
        }
        finally
        {

        }
    }

    public static ITrade getTradeBean(BaseReqData brd) throws Exception
    {
        String tradeTypeCode = brd.getTradeType().getTradeTypeCode();
        if (tradesMap.containsKey(tradeTypeCode))
        {
            return (ITrade) tradesMap.get(tradeTypeCode);
        }
        else
        {
            IData tradeCfg = BofQuery.getTradeCfgByCode(tradeTypeCode);
            if (IDataUtil.isEmpty(tradeCfg))
            {
                CSAppException.apperr(BofException.CRM_BOF_021, tradeTypeCode);
            }

            String className = tradeCfg.getString("CLASS_NAME").trim();
            ITrade tradeBean = (ITrade) Class.forName(className).newInstance();
            TradeProxy factory = new TradeProxy(tradeBean);
            ITrade iTrade = (ITrade) Proxy.newProxyInstance(tradeBean.getClass().getClassLoader(), tradeBean.getClass().getInterfaces(), factory);

            tradesMap.put(className, iTrade);
            return iTrade;
        }
    }
}
