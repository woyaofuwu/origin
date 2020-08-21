
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;

public final class TradeBack
{
    private final static Logger logger = Logger.getLogger(TradeBack.class);

    public static void back(IData mainTrade) throws Exception
    {
        back(mainTrade, null);
    }
    
    public static void back(IData mainTrade, String routeId) throws Exception
    {
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

        // 是否备份
        Boolean back = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_BACK, false);

        if (back == false)
        {
            return;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("订单备份开始");
        }

        String tradeId = mainTrade.getString("TRADE_ID");
        String acceptMonth = mainTrade.getString("ACCEPT_MONTH");
        String canceltag = mainTrade.getString("CANCEL_TAG");

        String[] paramName =
        { "IN_TRADE_ID", "IN_ACCEPT_MONTH", "IN_CANCEL_TAG", "OUT_RESULT_CODE", "OUT_RESULT_INFO" };

        IData paramValue = new DataMap();
        paramValue.put("IN_TRADE_ID", tradeId);
        paramValue.put("IN_ACCEPT_MONTH", acceptMonth);
        paramValue.put("IN_CANCEL_TAG", canceltag);

        // 得到proc名称
        String procName = BizEnv.getEnvString("crm.pk.tradeback", "PK_CS_TRADEBACK.MAIN");

        Dao.callProc(procName, paramName, paramValue, routeId);

        // 是否成功
        String resultCode = paramValue.getString("OUT_RESULT_CODE");

        if (!"0".equals(resultCode))
        {
            String resultInfo = paramValue.getString("OUT_RESULT_INFO");

            if (logger.isDebugEnabled())
            {
                logger.debug("订单备份返回，OUT_RESULT_CODE=[" + resultCode + "] OUT_RESULT_INFO=[" + resultInfo + "]");
            }

            CSAppException.apperr(TradeException.CRM_TRADE_52d, resultCode, resultInfo);
        }
    }
}
