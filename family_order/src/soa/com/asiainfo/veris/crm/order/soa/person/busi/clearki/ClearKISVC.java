
package com.asiainfo.veris.crm.order.soa.person.busi.clearki;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class ClearKISVC extends CSBizService
{

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = -1756769134328846009L;

    public IDataset clearKI(IData param) throws Exception
    {
        String simCardNo = param.getString("SIM_CARD_NO");
        String switchId = param.getString("SWITCH_ID");
        String serialNumber = param.getString("SERIAL_NUMBER");
        String imis = param.getString("IMIS");
        String staffId = CSBizBean.getVisit().getStaffId();
        String dataCode = param.getString("DATA_CODE");
        String batchId = param.getString("BATCH_ID");
        String tradeId = SeqMgr.getTradeId();
        String custId = SeqMgr.getCustId();
        String orderId = SeqMgr.getOrderId();
        String userId = SeqMgr.getUserId();

        IData tradeData = new DataMap();
        tradeData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        tradeData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        tradeData.put("ACCT_ID", "");
        tradeData.put("ACCT_ID_B", "");
        tradeData.put("ADVANCE_PAY", "0");
        tradeData.put("BATCH_ID", batchId);
        tradeData.put("BPM_ID", "");
        tradeData.put("BRAND_CODE", "ZZZZ");
        tradeData.put("CAMPN_ID", "");
        tradeData.put("CANCEL_CITY_CODE", "");
        tradeData.put("CANCEL_DATE", "");
        tradeData.put("CANCEL_DEPART_ID", "");
        tradeData.put("CANCEL_EPARCHY_CODE", "");
        tradeData.put("CANCEL_STAFF_ID", "");
        tradeData.put("CANCEL_TAG", "0");
        tradeData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        tradeData.put("CUST_CONTACT_ID", "");
        tradeData.put("CUST_ID", custId);
        tradeData.put("CUST_ID_B", "");
        tradeData.put("CUST_NAME", "海南移动");
        tradeData.put("EPARCHY_CODE", "0898");
        tradeData.put("EXEC_ACTION", "");
        tradeData.put("EXEC_DESC", "");
        tradeData.put("EXEC_RESULT", "");
        tradeData.put("EXEC_TIME", SysDateMgr.getSysTime());
        tradeData.put("FEE_STAFF_ID", "");
        tradeData.put("FEE_STATE", "0");
        tradeData.put("FEE_TIME", SysDateMgr.getSysTime());
        tradeData.put("FINISH_DATE", "");
        tradeData.put("FOREGIFT", "0");
        tradeData.put("FREE_RESOURCE_TAG", "");
        tradeData.put("INTF_ID", "");
        tradeData.put("INVOICE_NO", "");
        tradeData.put("IN_MODE_CODE", "0");
        tradeData.put("IS_NEED_HUMANCHECK", "");
        tradeData.put("NET_TYPE_CODE", "ZZ");
        tradeData.put("NEXT_DEAL_TAG", "0");
        tradeData.put("OLCOM_TAG", "1");
        tradeData.put("OPER_FEE", "0");
        tradeData.put("ORDER_ID", orderId);
        tradeData.put("PF_TYPE", "0");
        tradeData.put("PF_WAIT", "");
        tradeData.put("PRIORITY", "500");
        tradeData.put("PROCESS_TAG_SET", "0");
        tradeData.put("PRODUCT_ID", "");
        tradeData.put("PROD_ORDER_ID", "-1");
        tradeData.put("REMARK", "G网清KI");
        tradeData.put("RSRV_STR1", imis);
        tradeData.put("RSRV_STR10", "");
        tradeData.put("RSRV_STR2", switchId);
        tradeData.put("RSRV_STR3", simCardNo);
        tradeData.put("RSRV_STR4", "");
        tradeData.put("RSRV_STR5", "");
        tradeData.put("RSRV_STR6", "");
        tradeData.put("RSRV_STR7", "");
        tradeData.put("RSRV_STR8", "");
        tradeData.put("RSRV_STR9", "");
        tradeData.put("SERIAL_NUMBER", serialNumber);
        tradeData.put("SERIAL_NUMBER_B", "");
        tradeData.put("SERV_REQ_ID", "");
        tradeData.put("SUBSCRIBE_STATE", "0");
        tradeData.put("SUBSCRIBE_TYPE", "0");
        tradeData.put("TERM_IP", "");
        tradeData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        tradeData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        tradeData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        tradeData.put("TRADE_ID", tradeId);
        tradeData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        tradeData.put("TRADE_TYPE_CODE", "800");
        tradeData.put("UPDATE_DEPART_ID", "");
        tradeData.put("UPDATE_STAFF_ID", "");
        tradeData.put("UPDATE_TIME", "");
        tradeData.put("USER_ID", userId);
        tradeData.put("USER_ID_B", "");

        IData orderData = new DataMap();
        orderData.put("CUST_NAME", "海南移动");
        orderData.put("PSPT_TYPE_CODE", "");
        orderData.put("PSPT_ID", "");
        orderData.put("EPARCHY_CODE", "0898");
        orderData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        orderData.put("AUTH_CODE", "");
        orderData.put("ACTOR_NAME", "");
        orderData.put("ACTOR_PHONE", "");
        orderData.put("ACTOR_PSPT_TYPE_CODE", "");
        orderData.put("ACTOR_PSPT_ID", "");
        orderData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        orderData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        orderData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        orderData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        orderData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        orderData.put("TERM_IP", "");
        orderData.put("OPER_FEE", "0");
        orderData.put("FOREGIFT", "0");
        orderData.put("ADVANCE_PAY", "0");
        orderData.put("INVOICE_NO", "");
        orderData.put("FEE_STATE", "0");
        orderData.put("FEE_TIME", SysDateMgr.getSysTime());
        orderData.put("FEE_STAFF_ID", "");
        orderData.put("PROCESS_TAG_SET", "");
        orderData.put("FINISH_DATE", "");
        orderData.put("EXEC_TIME", SysDateMgr.getSysTime());
        orderData.put("EXEC_ACTION", "");
        orderData.put("EXEC_RESULT", "");
        orderData.put("EXEC_DESC", "");
        orderData.put("CUST_IDEA", "");
        orderData.put("HQ_TAG", "");
        orderData.put("DECOMPOSE_RULE_ID", "0");
        orderData.put("DISPATCH_RULE_ID", "0");
        orderData.put("CUST_CONTACT_ID", "0");
        orderData.put("SERV_REQ_ID", "");
        orderData.put("CONTRACT_ID", "");
        orderData.put("SOLUTION_ID", "0");
        orderData.put("CANCEL_TAG", "0");
        orderData.put("CANCEL_DATE", "");
        orderData.put("CANCEL_STAFF_ID", "");
        orderData.put("CANCEL_DEPART_ID", "");
        orderData.put("CANCEL_CITY_CODE", "");
        orderData.put("CANCEL_EPARCHY_CODE", "");
        orderData.put("UPDATE_TIME", "");
        orderData.put("UPDATE_STAFF_ID", "");
        orderData.put("UPDATE_DEPART_ID", "");
        orderData.put("REMARK", "G网清KI");
        orderData.put("RSRV_STR1", imis);
        orderData.put("RSRV_STR2", switchId);
        orderData.put("RSRV_STR3", simCardNo);
        orderData.put("RSRV_STR4", "");
        orderData.put("RSRV_STR5", "");
        orderData.put("RSRV_STR6", "");
        orderData.put("RSRV_STR7", "");
        orderData.put("RSRV_STR8", "");
        orderData.put("RSRV_STR9", "");
        orderData.put("RSRV_STR10", "");
        orderData.put("ORDER_INSTANCE_STATE", "");
        orderData.put("APP_TYPE", "");
        orderData.put("PRIORITY_TYPE", "");
        orderData.put("TRADE_DBSRCNAMES", "");
        orderData.put("IS_NEED_HUMANCHECK", "");
        orderData.put("ORDER_KIND_CODE", "");
        orderData.put("SUBSCRIBE_TYPE", "0");
        orderData.put("ORDER_ID", orderId);
        orderData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(orderId));
        orderData.put("BATCH_ID", batchId);
        orderData.put("BATCH_COUNT", "0");
        orderData.put("SUCC_TOTAL", "1");
        orderData.put("FAIL_TOTAL", "0");
        orderData.put("ORDER_TYPE_CODE", "800");
        orderData.put("TRADE_TYPE_CODE", "800");
        orderData.put("PRIORITY", "500");
        orderData.put("ORDER_STATE", "0");
        orderData.put("NEXT_DEAL_TAG", "0");
        orderData.put("IN_MODE_CODE", "0");
        orderData.put("CUST_ID", custId);

        Dao.insert("TF_B_TRADE", tradeData);
        Dao.insert("TF_B_ORDER", orderData);

        ResCall.clearKI(simCardNo, staffId, dataCode);

        IData result = new DataMap();
        result.put("ORDER_ID", orderId);
        result.put("TRADE_ID", tradeId);

        IDataset results = new DatasetList();
        results.add(result);

        return results;
    }

}
