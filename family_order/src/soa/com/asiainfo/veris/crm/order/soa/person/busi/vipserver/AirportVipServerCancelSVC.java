
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.AirportVipInfoQry;

public class AirportVipServerCancelSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 598810070326252442L;

    public void afterRegisterTradeInfo(String TRADE_ID) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("STATE", "1");
        inparams.put("RESERVICE_ID", TRADE_ID);
        inparams.put("RSRV_TAG1", "0");
        inparams.put("SERVICE_ID", TRADE_ID);
        inparams.put("RETURN_STAFF", getVisit().getStaffId());
        inparams.put("RETURN_EXPLAIN", "");
        inparams.put("REMARK", "VIP机场易登机服务返销");
        Dao.executeUpdateByCodeCode("TF_B_VIPAIRDROME_SERVICE", "RETURN_SERVICE", inparams);
    }

    public IDataset queryAirportCancel(IData params) throws Exception
    {
        String QUERY_TYPE = params.getString("QUERY_TYPE");
        String SERIAL_NUMBER = params.getString("SERIAL_NUMBER");
        String START_DATE = params.getString("START_DATE");
        String END_DATE = params.getString("END_DATE");
        String IS_TRUE = params.getString("IS_TRUE");
        String IS_FREE = params.getString("IS_FREE");
        return AirportVipInfoQry.queryAirportCancel(QUERY_TYPE, SERIAL_NUMBER, START_DATE, END_DATE, IS_TRUE, IS_FREE);
    }

    public IDataset submitButton(IData params) throws Exception
    {
        String TRADE_ID = params.getString("TRADE_ID");
        String SERIAL_NUMBER = params.getString("SERIAL_NUMBER");
        // inMap.put("TRADE_ID", inparams.getString("TRADE_ID", ""));
        // inMap.put("IN_MODE_CODE", inparams.getString("IN_MODE_CODE", "0"));
        // 返销登记流程，在tf_bh_trade修改记录的cancel_tag为1，同时在tf_b_trade表中新增一条cancel_tag为2的数据
        // TCS_CreateUndoTrade为公用的返销登记流程
        // TuxedoHelper.callTuxSvc(pd, "TCS_CreateUndoTrade", inMap, false);
        // BPM调用返销流程UNDO_TCS_FinishIBVipServerReg

        // 调用返销流程
        IData inMap = new DataMap();
        inMap.put("TRADE_ID", TRADE_ID);
        inMap.put("IN_MODE_CODE", params.getString("IN_MODE_CODE", "0"));
        inMap.put("REMARK", "VIP机场易登机服务返销");
        inMap.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());
        inMap.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inMap.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        inMap.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inMap.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartCode());
        inMap.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", inMap);
        afterRegisterTradeInfo(TRADE_ID);
        return null;
    }

}
