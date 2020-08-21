package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeExtInfoQry;
import org.apache.log4j.Logger;


/**
 * 中小企业 快速受理
 *
 *      业务受理成功后，驱动流程action;
 *
 * */
public class TradeFinishCallMinorecAction implements ITradeFinishAction
{
    private static Logger logger = Logger.getLogger(TradeFinishCallMinorecAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {

        logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程 TradeFinishCallMinorecAction >>>>>>>>>>>>>>>>>>");

        String tradeId = mainTrade.getString("TRADE_ID");

        logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程TradeFinishCallMinorecAction >>>>" + tradeId + ">>>>>>>>>>>>>>");

        IDataset extTrades = TradeExtInfoQry.getTradeEsopInfoTradeId(tradeId);

        if (IDataUtil.isEmpty(extTrades))  return;


        IData extTrade = extTrades.first();
        String eosTag = extTrade.getString("RSRV_STR10");

        if (!"EOS".equals(eosTag))
            return;

        // 集团专线的特殊处理,开户变更,改由pboss调用esop接口
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE", "");
        String userId = mainTrade.getString("USER_ID", "");
        String productId = mainTrade.getString("PRODUCT_ID", "");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER", "");

        /**
         * 3880  集团 多媒体桌面电话 新增
         * 3830  集团 融合v网 新增
         * 3621  集团 商务宽带 新增
         * 3030  集团 集团v网 新增
         *
         * 3883  集团 多媒体桌面电话 注销
         * 3623  集团 商务宽带 注销
         * 3833  集团 融合v网 注销
         * 3033  集团 集团v网 注销
         *
         * 3884  多媒体桌面电话成员新增
         * 3834  融合v网 成员新增
         * 3034  集团v网 成员新增
         *
         * 3887  多媒体桌面电话 成员注销
         * 3837  融合v网  成员注销
         * 3037  集团v网  成员注销
         *
         * 600   成员宽带 新增
         * 601   成员宽带 变更
         * 605   成员宽带 注销
         */

        if ("3880".equals(tradeTypeCode) || "3830".equals(tradeTypeCode) || "3621".equals(tradeTypeCode)||"3030".equals(tradeTypeCode)
                ||"3883".equals(tradeTypeCode) || "3623".equals(tradeTypeCode) || "3833".equals(tradeTypeCode)||"3033".equals(tradeTypeCode)
                || "3884".equals(tradeTypeCode) || "3834".equals(tradeTypeCode) || "3034".equals(tradeTypeCode)
                || "3887".equals(tradeTypeCode)  || "3837".equals(tradeTypeCode)  || "3037".equals(tradeTypeCode)
                || "600".equals(tradeTypeCode) || "601".equals(tradeTypeCode) || "605".equals(tradeTypeCode)
                )
        {
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程 TradeFinishCallMinorecAction 的判断业务类型>>>>>>>>>>>>>>>>>>");

            /* params.put("BUSIFORM_ID", extTrade.getString("RSRV_STR4")); 流程标识ID
            *  params.put("NODE_ID", extTrade.getString("RSRV_STR1")); // 流程节点 //推动流程
            *  CSAppCall.call("SS.WorkformDriveSVC.execute", params);
            */
            IData params = new DataMap();
            params.put("IBSYSID", extTrade.getString("ATTR_VALUE"));
            params.put("NODE_ID", extTrade.getString("RSRV_STR1"));
            params.put("BUSIFORM_ID", extTrade.getString("RSRV_STR8"));
            params.put("PRODUCT_ID", productId);
            params.put("ORDER_ID", mainTrade.getString("ORDER_ID", ""));
            params.put("USER_ID", userId);
            params.put("BATCH_ID", mainTrade.getString("BATCH_ID", ""));
            params.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER", ""));
            params.put("TRADE_ID", mainTrade.getString("TRADE_ID", ""));
            params.put("TRADE_TYPE_CODE", tradeTypeCode);
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("RECORD_NUM", extTrade.getString("RSRV_STR6"));

            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程ACTION>>>>>>>>>>>>>>>>>>" + params.toString());

            CSAppCall.call("SS.EopIntfSVC.saveEopNodeAndDrive", params);
            // 完工保存checkinWorkSheet数据
            CSAppCall.call("SS.WorkformCheckinSVC.record", params);

        }

    }

}
