
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeExtInfoQry;

public final class TradeEsop
{
    private final static Logger logger = Logger.getLogger(TradeEsop.class);

    public static void callEsopIntf(IData mainTrade) throws Exception
    {

        String tradeId = mainTrade.getString("TRADE_ID");

        // 获取ESOP信息
        IDataset tradeExts = TradeExtInfoQry.getTradeEsopExtByTradeId(tradeId);

        if (IDataUtil.isEmpty(tradeExts))
        {
            return;
        }

        IData tradeExt = tradeExts.getData(0);
        String brandCode = mainTrade.getString("BRAND_CODE");

        IData esopParam = new DataMap();
        esopParam.put("BIPCODE", "EOS2D011");
        esopParam.put("ACTIVITYCODE", "T2011011");
        esopParam.put("IBSYSID", tradeExt.getString("ATTR_VALUE"));
        esopParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        esopParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        esopParam.put("X_TRANS_CODE", "ITF_EOS_TcsGrpBusi");
        esopParam.put("X_SUBTRANS_CODE", "SendEosMessage");
        esopParam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        if ("BOSG".equals(brandCode))
        {
            esopParam.put("WORK_TYPE", "04");
            esopParam.put("PRODUCT_ID", "");
        }
        else
        {

            esopParam.put("WORK_TYPE", "01");

        }

        // IDataset resultset= CSAppCall.call("ITF_EOS_TcsGrpBusi", esopParam);
    }
}
