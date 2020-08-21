
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserForegiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trademgr.TradeArchProc;

public final class TradeArch
{
    private final static Logger logger = Logger.getLogger(TradeArch.class);

    public static void arch(IData mainTrade) throws Exception
    {
        arch(mainTrade, null);
    }

    public static void arch(IData mainTrade, String routeId) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("订单归档开始");
        }

        String tradeId = mainTrade.getString("TRADE_ID");
        String acceptMonth = mainTrade.getString("ACCEPT_MONTH");
        String canceltag = mainTrade.getString("CANCEL_TAG");

//        String[] paramName =
//        { "IN_TRADE_ID", "IN_ACCEPT_MONTH", "IN_CANCEL_TAG", "OUT_RESULT_CODE", "OUT_RESULT_INFO" };
//
//        IData paramValue = new DataMap();
//        paramValue.put("IN_TRADE_ID", tradeId);
//        paramValue.put("IN_ACCEPT_MONTH", acceptMonth);
//        paramValue.put("IN_CANCEL_TAG", canceltag);
//
//        // 得到proc名称
//        String procName = BizEnv.getEnvString("crm.pk.tradearch", "PK_CS_TRADEARCH.MAIN");

        
//        Dao.callProc(procName, paramName, paramValue, routeId);
        
        //订单归档
        TradeArchProc.executeProc(tradeId, acceptMonth, canceltag);

//        // 是否成功
//        String resultCode = paramValue.getString("OUT_RESULT_CODE");
//
//        if (!"0".equals(resultCode))
//        {
//            String resultInfo = paramValue.getString("OUT_RESULT_INFO");
//
//            if (logger.isDebugEnabled())
//            {
//                logger.debug("订单归档返回，OUT_RESULT_CODE=[" + resultCode + "] OUT_RESULT_INFO=[" + resultInfo + "]");
//            }
//
//            CSAppException.apperr(TradeException.CRM_TRADE_52b, resultCode, resultInfo);
//        }
    }
    
    public static void archUndo(IData mainTrade) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("订单返销开始");
        }
        TradeUndo.undoTrade(mainTrade);

        // 处理押金返销
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset feeSubInfos = TradefeeSubInfoQry.getFeeListByTrade(tradeId);
        if (IDataUtil.isNotEmpty(feeSubInfos))
        {
            IDataset foregiftInfos = DataHelper.filter(feeSubInfos, "FEE_MODE=1");// 取押金
            if (IDataUtil.isNotEmpty(foregiftInfos))
            {

                for (int i = 0, size = foregiftInfos.size(); i < size; i++)
                {
                    String tempUserId = foregiftInfos.getData(i).getString("USER_ID");
                    String feeTypeCode = foregiftInfos.getData(i).getString("FEE_TYPE_CODE");
                    long money = foregiftInfos.getData(i).getLong("FEE", 0L);

                    IDataset userForeGift = UserForegiftInfoQry.getUserForegift(tempUserId, feeTypeCode);
                    if (IDataUtil.isNotEmpty(userForeGift))
                    {
                        for (int j = 0, uSize = userForeGift.size(); j < uSize; j++)
                        {
                            IData tempData = userForeGift.getData(j);
                            tempData.put("MONEY", tempData.getLong("MONEY", 0L) - money);
                            tempData.put("UPDATE_TIME", mainTrade.getString("UPDATE_TIME"));
                            tempData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
                            tempData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
                            Dao.save("TF_F_USER_FOREGIFT", tempData);
                        }
                    }
                }
            }
        }
    }

}
