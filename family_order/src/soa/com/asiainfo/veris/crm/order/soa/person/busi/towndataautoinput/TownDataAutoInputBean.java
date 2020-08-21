
package com.asiainfo.veris.crm.order.soa.person.busi.towndataautoinput;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class TownDataAutoInputBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(TownDataAutoInputBean.class);

    /**
     * 工单表、订单表登记
     * 
     * @param data
     * @return orderId
     * @throws Exception
     *             wangjx 2013-9-4
     */
    public void insMainTrade(IData data) throws Exception
    {
        String orderId = SeqMgr.getOrderId();
        String tradeId = SeqMgr.getTradeId();
        String sysDate = SysDateMgr.getSysTime();

        // 主台账
        IData mainTradeData = new DataMap();
        mainTradeData.put("TRADE_ID", tradeId);
        mainTradeData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        mainTradeData.put("ORDER_ID", orderId);
        mainTradeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        mainTradeData.put("PRIORITY", "250");
        mainTradeData.put("SUBSCRIBE_TYPE", "0");
        mainTradeData.put("SUBSCRIBE_STATE", "0");
        mainTradeData.put("NEXT_DEAL_TAG", "0");
        mainTradeData.put("IN_MODE_CODE", getVisit().getInModeCode());
        mainTradeData.put("CUST_ID", "-1");
        mainTradeData.put("CUST_NAME", data.getString("CUST_NAME", ""));
        mainTradeData.put("USER_ID", data.getString("USER_ID", tradeId));
        mainTradeData.put("ACCT_ID", "-1");
        mainTradeData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        mainTradeData.put("NET_TYPE_CODE", "00");
        mainTradeData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        mainTradeData.put("CITY_CODE", getVisit().getCityCode());
        mainTradeData.put("SERIAL_NUMBER_B", "");
        mainTradeData.put("ACCEPT_DATE", sysDate);
        mainTradeData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        mainTradeData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        mainTradeData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        mainTradeData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        mainTradeData.put("TERM_IP", getVisit().getRemoteAddr());
        mainTradeData.put("OPER_FEE", data.getString("OPER_FEE", "0"));
        mainTradeData.put("ADVANCE_PAY", data.getString("ADVANCE_PAY", "0"));
        mainTradeData.put("FOREGIFT", data.getString("FOREGIFT", "0"));
        mainTradeData.put("FEE_STATE", "0");
        mainTradeData.put("PROCESS_TAG_SET", BofConst.PROCESS_TAG_SET);
        mainTradeData.put("OLCOM_TAG", "1");
        mainTradeData.put("EXEC_TIME", sysDate);
        mainTradeData.put("CANCEL_TAG", BofConst.CANCEL_TAG_NO);
        mainTradeData.put("UPDATE_TIME", sysDate);
        mainTradeData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        mainTradeData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        mainTradeData.put("REMARK", data.getString("REMARK", "小区数据自动制作业务"));
        mainTradeData.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
        mainTradeData.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
        mainTradeData.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
        mainTradeData.put("RSRV_STR4", data.getString("RSRV_STR4", ""));
        mainTradeData.put("RSRV_STR5", data.getString("RSRV_STR5", ""));
        mainTradeData.put("RSRV_STR6", data.getString("RSRV_STR6", ""));
        mainTradeData.put("RSRV_STR7", data.getString("RSRV_STR7", ""));
        mainTradeData.put("RSRV_STR8", data.getString("RSRV_STR8", ""));
        mainTradeData.put("RSRV_STR9", data.getString("RSRV_STR9", ""));
        mainTradeData.put("RSRV_STR10", data.getString("RSRV_STR10", ""));
        mainTradeData.put("PF_WAIT", "0");

        Dao.insert("TF_B_TRADE", mainTradeData, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

        // 客户订单
        IData orderTradeData = new DataMap();
        orderTradeData.put("ORDER_ID", orderId);
        orderTradeData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(orderId));
        orderTradeData.put("ORDER_TYPE_CODE", data.getString("ORDER_TYPE_CODE", data.getString("TRADE_TYPE_CODE")));
        orderTradeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        orderTradeData.put("PRIORITY", "250");
        orderTradeData.put("ORDER_STATE", "0");
        orderTradeData.put("NEXT_DEAL_TAG", "0");
        orderTradeData.put("IN_MODE_CODE", getVisit().getInModeCode());
        orderTradeData.put("CUST_ID", data.getString("CUST_ID", "0"));
        orderTradeData.put("CUST_NAME", data.getString("CUST_NAME", ""));
        orderTradeData.put("PSPT_TYPE_CODE", data.getString("PSPT_TYPE_CODE", ""));
        orderTradeData.put("PSPT_ID", data.getString("PSPT_ID", ""));
        orderTradeData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        orderTradeData.put("CITY_CODE", getVisit().getCityCode());
        orderTradeData.put("ACCEPT_DATE", sysDate);
        orderTradeData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        orderTradeData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        orderTradeData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        orderTradeData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        orderTradeData.put("TERM_IP", getVisit().getRemoteAddr());
        orderTradeData.put("OPER_FEE", data.getString("OPER_FEE", "0"));
        orderTradeData.put("ADVANCE_PAY", data.getString("ADVANCE_PAY", "0"));
        orderTradeData.put("FOREGIFT", data.getString("FOREGIFT", "0"));
        orderTradeData.put("FEE_STATE", "0");
        orderTradeData.put("PROCESS_TAG_SET", BofConst.PROCESS_TAG_SET);
        orderTradeData.put("EXEC_TIME", sysDate);
        orderTradeData.put("CANCEL_TAG", BofConst.CANCEL_TAG_NO);
        orderTradeData.put("UPDATE_TIME", sysDate);
        orderTradeData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        orderTradeData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        orderTradeData.put("SUBSCRIBE_TYPE", "0");// 默认，没有AEE不执行

        Dao.insert("TF_B_ORDER", orderTradeData, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

        IData staffTradeData = new DataMap();
        staffTradeData.put("TRADE_ID", tradeId);
        staffTradeData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        staffTradeData.put("ORDER_ID", orderId);
        staffTradeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        staffTradeData.put("PRIORITY", "250");
        staffTradeData.put("SUBSCRIBE_TYPE", "0");
        staffTradeData.put("SUBSCRIBE_STATE", "0");
        staffTradeData.put("NEXT_DEAL_TAG", "0");
        staffTradeData.put("IN_MODE_CODE", getVisit().getInModeCode());
        staffTradeData.put("CUST_ID", "-1");
        staffTradeData.put("CUST_NAME", data.getString("CUST_NAME", ""));
        staffTradeData.put("USER_ID", data.getString("USER_ID", tradeId));
        staffTradeData.put("ACCT_ID", "-1");
        staffTradeData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        staffTradeData.put("NET_TYPE_CODE", "00");
        staffTradeData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        staffTradeData.put("CITY_CODE", getVisit().getCityCode());
        staffTradeData.put("SERIAL_NUMBER_B", "");
        staffTradeData.put("ACCEPT_DATE", sysDate);
        staffTradeData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        staffTradeData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        staffTradeData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        staffTradeData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        staffTradeData.put("TERM_IP", getVisit().getRemoteAddr());
        staffTradeData.put("OPER_FEE", data.getString("OPER_FEE", "0"));
        staffTradeData.put("ADVANCE_PAY", data.getString("ADVANCE_PAY", "0"));
        staffTradeData.put("FOREGIFT", data.getString("FOREGIFT", "0"));
        staffTradeData.put("FEE_STATE", "0");
        staffTradeData.put("PROCESS_TAG_SET", BofConst.PROCESS_TAG_SET);
        staffTradeData.put("OLCOM_TAG", "0");
        staffTradeData.put("EXEC_TIME", sysDate);
        staffTradeData.put("CANCEL_TAG", BofConst.CANCEL_TAG_NO);
        staffTradeData.put("UPDATE_TIME", sysDate);
        staffTradeData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        staffTradeData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        staffTradeData.put("REMARK", data.getString("REMARK", "小区数据自动制作业务"));
        staffTradeData.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
        staffTradeData.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
        staffTradeData.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
        staffTradeData.put("RSRV_STR4", data.getString("RSRV_STR4", ""));
        staffTradeData.put("RSRV_STR5", data.getString("RSRV_STR5", ""));
        staffTradeData.put("RSRV_STR6", data.getString("RSRV_STR6", ""));
        staffTradeData.put("RSRV_STR7", data.getString("RSRV_STR7", ""));
        staffTradeData.put("RSRV_STR8", data.getString("RSRV_STR8", ""));
        staffTradeData.put("RSRV_STR9", data.getString("RSRV_STR9", ""));
        staffTradeData.put("RSRV_STR10", data.getString("RSRV_STR10", ""));
        staffTradeData.put("DAY", SysDateMgr.getCurDay());

        Dao.insert("TF_BH_TRADE_STAFF", staffTradeData, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

    }
}
