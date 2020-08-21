
package com.asiainfo.veris.crm.order.soa.person.busi.uec;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class UECAcitiyRegTradeSVC extends CSBizService
{

    public void recordHisMainTrade(IData data) throws Exception
    {
        IData iparam = new DataMap();

        iparam.put("TRADE_ID", data.getString("TRADE_ID", ""));

        iparam.put("BATCH_ID", "");

        iparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("TRADE_ID", "")));

        iparam.put("ORDER_ID", "");

        iparam.put("PROD_ORDER_ID", "");

        iparam.put("BPM_ID", "");

        iparam.put("CAMPN_ID", "");

        iparam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", ""));

        iparam.put("PRIORITY", "0");

        iparam.put("SUBSCRIBE_TYPE", "0");

        iparam.put("SUBSCRIBE_STATE", "9");

        iparam.put("NEXT_DEAL_TAG", "0");

        iparam.put("IN_MODE_CODE", getVisit().getInModeCode());

        iparam.put("USER_ID", data.getString("USER_ID", ""));

        iparam.put("CUST_NAME", data.getString("CUST_NAME", ""));

        iparam.put("ACCT_ID", data.getString("ACCT_ID", ""));

        iparam.put("CUST_ID", data.getString("CUST_ID", ""));

        iparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));

        iparam.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", "00"));

        iparam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", ""));

        iparam.put("CITY_CODE", data.getString("CITY_CODE", ""));

        iparam.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));

        iparam.put("BRAND_CODE", data.getString("BRAND_CODE", ""));

        iparam.put("CUST_ID_B", "");

        iparam.put("ACCT_ID_B", "");

        iparam.put("USER_ID_B", "");

        iparam.put("SERIAL_NUMBER_B", "");

        iparam.put("CUST_CONTACT_ID", "");

        iparam.put("SERV_REQ_ID", "");

        iparam.put("INTF_ID", "");

        iparam.put("ACCEPT_DATE", data.getString("ACCEPT_DATE", ""));

        iparam.put("TRADE_STAFF_ID", getVisit().getStaffId());

        iparam.put("TRADE_DEPART_ID", getVisit().getDepartId());

        iparam.put("TRADE_CITY_CODE", getVisit().getCityCode());

        iparam.put("TRADE_EPARCHY_CODE", getTradeEparchyCode());

        iparam.put("TERM_IP", getVisit().getLoginIP());

        iparam.put("OPER_FEE", "0");

        iparam.put("FOREGIFT", "0");

        iparam.put("ADVANCE_PAY", "0");

        iparam.put("INVOICE_NO", "");

        iparam.put("FEE_STATE", "0");

        iparam.put("FEE_TIME", "");

        iparam.put("FEE_STAFF_ID", "");

        iparam.put("PROCESS_TAG_SET", "0");

        iparam.put("OLCOM_TAG", "0");

        iparam.put("FINISH_DATE", data.getString("ACCEPT_DATE", ""));

        iparam.put("EXEC_TIME", data.getString("ACCEPT_DATE", ""));

        iparam.put("EXEC_ACTION", "0");

        iparam.put("EXEC_RESULT", "");

        iparam.put("EXEC_DESC", data.getString("EXEC_DESC", ""));

        iparam.put("CANCEL_TAG", "0");

        iparam.put("CANCEL_DATE", "");

        iparam.put("CANCEL_STAFF_ID", "");

        iparam.put("CANCEL_DEPART_ID", "");

        iparam.put("CANCEL_CITY_CODE", "");

        iparam.put("CANCEL_EPARCHY_CODE", "");

        iparam.put("UPDATE_TIME", data.getString("ACCEPT_DATE", ""));

        iparam.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID", ""));

        iparam.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID", ""));

        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_ALL", iparam,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public void recordSubTrade(IData data) throws Exception
    {

        // 子台帐
        IData subTrade = new DataMap();
        subTrade.put("TRADE_ID", data.getString("TRADE_ID", ""));
        subTrade.put("ACTIVITY_NUMBER", data.getString("ACTIVITY_NUMBER"));
        subTrade.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("ACCEPT_DATE")));
        subTrade.put("USER_ID", data.getString("USER_ID", ""));
        subTrade.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        subTrade.put("CITY_CODE", data.getString("CITY_CODE", ""));
        subTrade.put("DEAL_FLAG", data.getString("DEAL_FLAG", "0"));
        subTrade.put("ACCEPT_DATE", data.getString("ACCEPT_DATE", ""));
        subTrade.put("PRIZE_TYPE_CODE", data.getString("PRIZE_TYPE_CODE", "0"));
        subTrade.put("EXEC_FLAG", data.getString("EXEC_FLAG", "0"));
        subTrade.put("EXEC_TIME", data.getString("EXEC_TIME", ""));
        subTrade.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        subTrade.put("UPDATE_DEPART_ID", getVisit().getDepartId());

        Dao.executeUpdateByCodeCode("SMS", "INS_UEC_LOTTERY_TRADE", subTrade,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public IData register(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "ACTIVITY_NUMBER");
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "USER_ID");
        IDataUtil.chkParam(data, "CITY_CODE");
        IDataUtil.chkParam(data, "DEAL_FLAG");
        IDataUtil.chkParam(data, "ACCEPT_DATE");
        IDataUtil.chkParam(data, "PRIZE_TYPE_CODE");
        IDataUtil.chkParam(data, "EXEC_FLAG");

        if ("1".equals(data.get("EXEC_FLAG")))
        {
            IDataUtil.chkParam(data, "EXEC_TIME");
        }

        String serialNumber = data.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_669);
        }
        String userid2 = user.getString("USER_ID", "");
        String userid1 = data.getString("USER_ID", "");
        if (userid2.equals(userid1) == false)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_913, serialNumber + "," + userid1 + "号码用户标识不匹配");
        }

        // 去掉老系统 setAutoCommit逻辑
        String tradeId = SeqMgr.getTradeId();
        data.put("TRADE_ID", tradeId);
        data.put("TRADE_TYPE_CODE", "305");
        // 记录子台账
        recordSubTrade(data);
        // 记录台账
        recordHisMainTrade(data);

        IData result = new DataMap();
        result.put("TRADE_ID", tradeId);
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "成功登记台账");

        return result;
    }
}
