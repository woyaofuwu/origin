
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

/**
 * 外围接口
 * 
 * @author think
 */
public class CCHTCall
{

    /**
     * 修改外围日志表
     * 
     * @param tradeEparchyCode
     * @param channelTradeId
     * @param channelAcceptTime
     * @param inModeCode
     * @param scoreValue
     * @throws Exception
     */
    public static void insertScoreTradeLog(String tradeEparchyCode, String channelTradeId, String channelAcceptTime, String inModeCode, String scoreValue) throws Exception
    {

        IData param = new DataMap();
        param.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        param.put("CHANNEL_TRADE_ID", channelTradeId);
        param.put("CHANNEL_ACCEPT_TIME", channelAcceptTime);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("SCORE_VALUE", scoreValue);
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        CSAppCall.callCCHT("ITF_CRM_InsertScoreTradeLog", param, false);
    }

    public static IDataset payCardGao(IData param) throws Exception
    {
        boolean dev = BizEnv.getEnvBoolean("crm.call.itf.ccht.paycardgro.dev", false);

        if (dev == true)
        {
            return new DatasetList();
        }

        IDataset dataset = CSAppCall.callCCHT("ITF_CCHT_PAYCARDGAO", param, false);

        return dataset;
    }

    /**
     * 二卡合一查询
     * 
     * @data 2013-9-24
     * @param carrier_id
     * @return
     * @throws Exception
     */
    public static IDataset queryTwoCardFeeInfo(String carrier_id, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("CARRIER_ID", carrier_id);
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset dataset = CSAppCall.callCCHT("ITF_ACCT_GetTwoCardFeeInfo", param, false);

        return dataset;
    }

    /**
     * 积分兑奖返销对账
     * 
     * @param routeEparchyCode
     * @param termIp
     * @param channelTradeId
     * @param channelAcceptTime
     * @param tradeTypeCode
     * @param tradeDepartPasswd
     * @throws Exception
     */
    public static void undoScoreTradeLog(String routeEparchyCode, String termIp, String channelTradeId, String channelAcceptTime, String tradeTypeCode, String tradeDepartPasswd) throws Exception
    {

        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
        param.put("TRADE_TERMINAL_ID", termIp);
        param.put("CHANNEL_TRADE_ID", channelTradeId);
        param.put("CHANNEL_ACCEPT_TIME", channelAcceptTime);
        param.put("PARA_CODE3", tradeTypeCode);
        param.put("TRADE_DEPART_PASSWD", tradeDepartPasswd);

        CSAppCall.callCCHT("ITF_ModifyCancelTagForCrm", param, false);
    }
}
