
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeExtInfoQry;

public class DataLineTradeFinshCallEsopAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");

        IDataset extTrades = TradeExtInfoQry.getTradeEsopInfoTradeId(tradeId);
        if (IDataUtil.isEmpty(extTrades))
            return;

        IData extTrade = extTrades.getData(0);
        String eosTag = extTrade.getString("RSRV_STR10");

        // 得到brand_code
        String brandCode = mainTrade.getString("BRAND_CODE");

        IData param = new DataMap();

        String workType = "";
        String productId = "";
        String bipCode = "EOS2D011";
        String activity = "T2011011";
        String X_TRANS_CODE = "ITF_EOS_TcsGrpBusi";
        String X_SUBTRANS_CODE = "SendEosMessage";
        if (!"EOS".equals(eosTag))
            return;

        if ("BOSG".equals(brandCode))
        {
            workType = "04";
            productId = "";
        }
        else
        {
            workType = "01";
        }

        param.put("WORK_TYPE", workType);
        param.put("BIPCODE", bipCode);
        param.put("ACTIVITYCODE", activity);
        param.put("IBSYSID", extTrade.getString("ATTR_VALUE"));
        param.put("PRODUCT_ID", productId);
        param.put("RSRV_STR37", extTrade.getString("RSRV_STR3", ""));
        param.put("X_TRANS_CODE", X_TRANS_CODE);
        param.put("X_SUBTRANS_CODE", X_SUBTRANS_CODE);
        param.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, mainTrade.getString("TRADE_EPARCHY_CODE"));

        ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", param);
    }

}
