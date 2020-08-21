
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * @des 本地积分兑换及网购平台积分兑换返销
 * @author huangsl
 */
public class TrainCancelSVC extends CSBizService
{
    public IData trainCancelTrade(IData data) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(data, Route.ROUTE_EPARCHY_CODE);
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "OPERATE_ID");
        IDataUtil.chkParam(data, "PROVINCE_CODE");
        IDataUtil.chkParam(data, "TRADE_EPARCHY_CODE");
        IDataUtil.chkParam(data, "TRADE_STAFF_ID");
        IDataUtil.chkParam(data, "TRADE_ID");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, data.getString("SERIAL_NUMBER"));
        }
        IDataset tradeHisDs = IDataUtil.idToIds(UTradeHisInfoQry.qryTradeHisByPk(data.getString("TRADE_ID"), "0", this.getTradeEparchyCode()));
        if (IDataUtil.isEmpty(tradeHisDs))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_11);
        }
        IData result = tradeHisDs.getData(0);
        if (!"Z".equals(result.getString("RSRV_STR9")) && !"ES".equals(result.getString("RSRV_STR9")) && !"WG".equals(result.getString("RSRV_STR9")))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_329);
        }
        
        //E拇指平台验证订单在经分是否已经被结算，如果被结算就无法进行返销
        String inModeCode=data.getString("IN_MODE_CODE","");
        if(inModeCode!=null&&inModeCode.equals("ES")){
        	IDataset accountTrades=TradeInfoQry.qryAccountScoreTradeInfo(data.getString("TRADE_ID"));
            if(IDataUtil.isNotEmpty(accountTrades)){
            	 CSAppException.apperr(TradeException.CRM_TRADE_334);
            }
        }
        
        // 调返销接口
        CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", data);
        IData returnData = new DataMap();
        returnData.put("X_RESULTCODE", "0");
        returnData.put("X_RESULTINFO", "返销登记成功！");
        return returnData;
    }
}
