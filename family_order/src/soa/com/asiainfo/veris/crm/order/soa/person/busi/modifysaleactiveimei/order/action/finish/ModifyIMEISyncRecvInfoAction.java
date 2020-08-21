
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.action.finish;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class ModifyIMEISyncRecvInfoAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        // 赞不需要在代码中插同步
        // FeeTradeData fd = new FeeTradeData();
        // fd.setRsrvStr1(mainTrade.getString("RSRV_STR1"));
        // fd.setRsrvStr2(mainTrade.getString("RSRV_STR2"));
        // fd.setRsrvStr3(mainTrade.getString("RSRV_STR3"));
        String relationTradeId = mainTrade.getString("RSRV_STR3");
        String newIMEI = mainTrade.getString("RSRV_STR1");
        String oldIMEI = mainTrade.getString("RSRV_STR2");
        String userId = mainTrade.getString("USER_ID");
        AcctCall.changeIMEISync(relationTradeId, oldIMEI, newIMEI, userId);
    }

    /*
     * @Override public void executeAction(IData mainTrade) throws Exception { // TODO Auto-generated method stub String
     * syncId = SeqMgr.getBillSynId(); String tradeId = mainTrade.getString("TRADE_ID"); String userId =
     * mainTrade.getString("USER_ID"); String serialNumber = mainTrade.getString("SERIAL_NUMBER"); String eparchyCode =
     * mainTrade.getString("EPARCHY_CODE"); String newIMEI = mainTrade.getString("RSRV_STR1"); String oldIMEI =
     * mainTrade.getString("RSRV_STR2"); String relationTradeId = mainTrade.getString("RSRV_STR3"); // 插入TI_A_SYNC_RECV
     * IData param = new DataMap(); String seqId = SeqMgr.getChargeId(); param.put("SYNC_SEQUENCE", syncId);
     * param.put("TRADE_ID", seqId); param.put("TRADE_TYPE_CODE", "7045"); param.put("BATCH_ID", seqId);
     * param.put("PRIORITY", "0"); param.put("CHARGE_ID", seqId); param.put("ACCT_ID", "0"); param.put("USER_ID",
     * userId); param.put("SERIAL_NUMBER", serialNumber); param.put("ACCT_ID2", ""); param.put("USER_ID2", "");
     * param.put("WRITEOFF_MODE", "1"); param.put("CHANNEL_ID", "15000"); param.put("PAYMENT_ID", "0");
     * param.put("PAY_FEE_MODE_CODE", "0"); param.put("PAYMENT_OP", "16000"); param.put("RECV_FEE", "0");
     * param.put("RECV_TIME", SysDateMgr.getSysTime()); param.put("RECV_EPARCHY_CODE", eparchyCode);
     * param.put("RECV_CITY_CODE", "0000"); param.put("RECV_DEPART_ID", "0000 "); param.put("RECV_STAFF_ID",
     * "0000    "); param.put("PAYMENT_REASON_CODE", "0"); param.put("ACT_TAG", "4"); param.put("ACTION_CODE", "-1");
     * param.put("REMARK", "购机活动修改IMEI号资料同步"); param.put("OUTER_TRADE_ID", relationTradeId); param.put("CANCEL_TAG",
     * "0"); param.put("DEAL_TAG", "0"); param.put("DEAL_TIME", ""); param.put("RESULT_CODE", "0");
     * param.put("RESULT_INFO", ""); param.put("START_CYCLE_ID", "190001"); param.put("END_CYCLE_ID", "205001");
     * param.put("START_DATE", ""); param.put("MONTHS", "240"); param.put("AMONTH", "0"); param.put("LIMIT_MONEY",
     * "-1"); param.put("BATDEALTIME", ""); param.put("MODIFY_TAG", "0"); param.put("VALID_TAG", "0");
     * param.put("RSRV_FEE1", "0"); param.put("RSRV_FEE2", "0"); param.put("RSRV_INFO1", newIMEI);// 新IMEI号
     * param.put("RSRV_DATE1", ""); param.put("RSRV_INFO2", oldIMEI);// 旧IMEI号 param.put("RSRV_INFO3",
     * relationTradeId);// 老的trade_id ,即 tf_f_user_sale_goods里的RELATION_TRADE_ID param.put("SYNC_DAY",
     * syncId.substring(6, 8)); Dao.executeUpdateByCodeCode("TI_A_SYNC_RECV", "INS_ALL", param); // 插入同步主表
     * param.clear(); param.put("SYNC_DAY", syncId.substring(6, 8)); param.put("SYNC_SEQUENCE", syncId);
     * param.put("TRADE_ID", tradeId); Dao.executeUpdateByCodeCode("TI_B_SYNCHINFO", "INS_SYNCHINFO", param); }
     */

}
