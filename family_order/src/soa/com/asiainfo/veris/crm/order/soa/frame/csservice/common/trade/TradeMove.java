
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;

public final class TradeMove
{
    private final static Logger logger = Logger.getLogger(TradeMove.class);

    public static void move(IData mainTrade) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("订单搬迁开始");
        }

        String tradeId = mainTrade.getString("TRADE_ID");
        String acceptMonth = mainTrade.getString("ACCEPT_MONTH");
        String canceltag = mainTrade.getString("CANCEL_TAG");

        // 是否搬迁到历史2表
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

        boolean bSecond = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_FINISH_MOVE_SECOND, false);

        String isSecond = "n";

        if (bSecond)
        {
            isSecond = "y";
        }

        String[] paramName =
        { "IN_TRADE_ID", "IN_ACCEPT_MONTH", "IN_CANCEL_TAG", "IN_IS_SECOND", "OUT_RESULT_CODE", "OUT_RESULT_INFO" };

        IData paramValue = new DataMap();
        paramValue.put("IN_TRADE_ID", tradeId);
        paramValue.put("IN_ACCEPT_MONTH", acceptMonth);
        paramValue.put("IN_CANCEL_TAG", canceltag);
        paramValue.put("IN_IS_SECOND", isSecond);

        // 得到proc名称
        String procName = BizEnv.getEnvString("crm.pk.trademove", "PK_CS_TRADEMOVE.MAIN");

        Dao.callProc(procName, paramName, paramValue,Route.getJourDb());

        // 是否成功
        String resultCode = paramValue.getString("OUT_RESULT_CODE");

        if (!"0".equals(resultCode))
        {
            String resultInfo = paramValue.getString("OUT_RESULT_INFO");

            if (logger.isDebugEnabled())
            {
                logger.debug("订单搬迁返回，OUT_RESULT_CODE=[" + resultCode + "] OUT_RESULT_INFO=[" + resultInfo + "]");
            }

            CSAppException.apperr(TradeException.CRM_TRADE_52c, resultCode, resultInfo);
        }

        // 处理激活
    }
}
