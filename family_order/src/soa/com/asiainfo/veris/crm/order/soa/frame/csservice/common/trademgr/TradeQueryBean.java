
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trademgr;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeMag;

public class TradeQueryBean
{
    public static IDataset qryErrorMsg(IData data) throws Exception
    {
        String tradeTable = data.getString("TRADE_TABLE");
        IData param = new DataMap(tradeTable);

        String tradeId = param.getString("TRADE_ID");
        String orderId = param.getString("ORDER_ID");
        String routeId = param.getString("ROUTE_ID");

        String errorMsg = TradeInfoQry.qryErrorMsg(tradeId, orderId, routeId);

        IData map = new DataMap();
        map.put("ERR", errorMsg);

        IDataset error = new DatasetList();
        error.add(map);

        return error;
    }

    public static IDataset queryTradeInfo(IData data) throws Exception
    {
        String routeId = data.getString("ROUTE_ID");
        String sn = data.getString("SERIAL_NUMBER");
        String orderId = data.getString("ORDER_ID");
        String tradeId = data.getString("TRADE_ID");

        IDataset tradeInfo = null;
        IData trade = null;

        // 根据tradeId查
        if (StringUtils.isNotEmpty(tradeId))
        {
            tradeInfo = TradeInfoQry.getMainTradeByTradeId(tradeId, routeId);
        }
        else if (StringUtils.isNotEmpty(sn))
        {
            tradeInfo = TradeInfoQry.getMainTradeBySn(sn, routeId);
        }
        else if (StringUtils.isNotEmpty(orderId))
        {
            // 先查sub表
            IDataset orderInfo = UOrderSubInfoQry.qryOrderSubByOrderId(orderId);

            // sub没有，查当前route
            if (IDataUtil.isEmpty(orderInfo))
            {
                tradeInfo = UTradeInfoQry.qryTradeByOrderId(orderId, routeId);
            }
            else
            {
                // 有sub，根据sub 一条条再查
                tradeInfo = new DatasetList();

                for (int i = 0, size = orderInfo.size(); i < size; i++)
                {
                    IData order = orderInfo.getData(i);

                    // 有，依次查trae信息
                    tradeId = order.getString("TRADE_ID");
                    routeId = order.getString("ROUTE_ID");

                    IDataset ids = TradeInfoQry.getMainTradeByTradeId(tradeId, routeId);

                    if (IDataUtil.isEmpty(ids))
                    {
                        continue;
                    }

                    IData map = ids.getData(0);

                    map.put("ROUTE_ID", routeId);

                    tradeInfo.add(map);
                }
            }
        }

        if (IDataUtil.isEmpty(tradeInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到订单信息");
        }

        for (int i = 0, size = tradeInfo.size(); i < size; i++)
        {
            trade = tradeInfo.getData(i);

            // tradetype
            String tradeTypeCode = trade.getString("TRADE_TYPE_CODE");
            String eparchyCode = trade.getString("EPARCHY_CODE");

            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode, eparchyCode);

            trade.put("TRADE_TYPE_NAME", "[" + tradeTypeCode + "]" + tradeTypeName);

            // olcom
            String olcom = trade.getString("OLCOM_TAG");
            String olcomName = "[0]无指令";

            if (StringUtils.isNotEmpty(olcom) && "1".equals(olcom))
            {
                olcomName = "[1]发指令";
            }

            trade.put("OLCOM_TAG_NAME", olcomName);

            // pf wait
            String wait = trade.getString("PF_WAIT");
            String waitName = "[0]开环";

            if (StringUtils.isNotEmpty(olcom) && "1".equals(olcom))
            {
                waitName = "[1]闭环";
            }

            trade.put("PF_WAIT_NAME", waitName);

            String tradeState = trade.getString("SUBSCRIBE_STATE");

            // tradestate
            String tradeStataName = StaticUtil.getStaticValue("TRADE_SUBSCRIBE_STATE", tradeState);

            trade.put("SUBSCRIBE_STATE_NAME", tradeStataName);

            if (!trade.containsKey("ROUTE_ID"))
            {
                trade.put("ROUTE_ID", routeId);
            }

            // 取tf_b_order表中的order_state
            IData orderinfo = UOrderInfoQry.qryOrderByOrderId(trade.getString("ORDER_ID"), trade.getString("ROUTE_ID"));
            if (IDataUtil.isNotEmpty(orderinfo))
            {
                trade.put("ORDER_STATE", orderinfo.getString("ORDER_STATE"));
            }
        }

        return tradeInfo;
    }

    public static IDataset tradePfAgain(IData data) throws Exception
    {
        String tradeTable = data.getString("TRADE_TABLE");

        IData param = new DataMap(tradeTable);

        String orderId = param.getString("ORDER_ID");
        String acceptMonth = param.getString("ACCEPT_MONTH");
        String cancelTag = param.getString("CANCEL_TAG", "0");
        String tradeId = param.getString("TRADE_ID");
        String routeId = param.getString("ROUTE_ID", Route.getCrmDefaultDb());
        String orderState = param.getString("ORDER_STATE");
        String tradeState = param.getString("SUBSCRIBE_STATE");
        String subType = param.getString("SUBSCRIBE_TYPE");

        
        if("300".equals(subType)){
        	IData pfData = new DataMap();
        	pfData.put("SUBSCRIBE_ID", param.getString("SUBSCRIBE_ID"));
        	IDataset resurnInfo = CSAppCall.call("PF_ORDER_WORKFORM_REDO", pfData);
        	if(IDataUtil.isEmpty(resurnInfo)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用服务开通重跑接口返回异常");
        	}else{
        		String resultType = resurnInfo.getData(0).getString("X_RESULTTYPE");
        		String resultCode = resurnInfo.getData(0).getString("X_RESULTCODE");
        		String resultInfo = resurnInfo.getData(0).getString("X_RESULTINFO");
        		if(!"0".equals(resultType)){
        			String errMsg = "服务开通重跑接口异常,X_RESULTTYPE:"+resultType+",X_RESULTCODE:"+resultCode+",X_RESULTINFO:"+resultInfo;
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, errMsg);
        		}
        	}
        	TradeInfoQry.updateTradeStateByTradeId(tradeId, acceptMonth, cancelTag, "0", routeId);
        	TradeInfoQry.updateTradeStateByTradeId(tradeId, acceptMonth, cancelTag, "0", routeId);
        	return null;
        }
        
        if ("3".equals(orderState) || "M".equals(tradeState))
        {
            TradeMag.updateStateByOrderId(orderId, "0", routeId);

            if ("M".equals(tradeState))
            {
                TradeInfoQry.updateTradeStateByTradeId(tradeId, acceptMonth, cancelTag, "0", routeId);
            }
        }

        if ("6".equals(tradeState))
        {
            TradeInfoQry.updateTradeStateByTradeId(tradeId, acceptMonth, cancelTag, "P", routeId);
        }
        
        
        return null;
    }
}
