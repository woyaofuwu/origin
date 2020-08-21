
package com.asiainfo.veris.crm.order.soa.person.busi.uec;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.GGCardInfoQry;

public class GGCardExchangeSVC extends CSBizService
{
    /**
     * 根据字符串日期获取日期 日期格式：yyyy-mm-dd hh24:mi:ss
     * 
     * @description
     * @date 2014-3-30
     */
    private static String getStringDateDay(String dateStr) throws Exception
    {
        String day = "";
        if (dateStr == null || "".equals(dateStr.trim()))
        {
            day = "";
        }
        if (dateStr.length() >= 10)
        {
            day = dateStr.substring(8, 10);
        }

        return day;
    }

    /**
     * 根据字符串日期获取月份 日期格式：yyyy-mm-dd hh24:mi:ss
     * 
     * @description
     * @date 2014-3-30
     */
    private static String getStringDateMonth(String dateStr) throws Exception
    {
        String month = "";
        if (dateStr == null || "".equals(dateStr.trim()))
        {
            month = "";
        }
        if (dateStr.length() >= 10)
        {
            month = dateStr.substring(5, 7);
        }

        return month;
    }

    /**
     * @description 调用计费账务接口充值
     * @date 2014-7-11
     */
    private void callActIntfPayFee(IData inData, int cardFee) throws Exception
    {
        if (cardFee > 0)
        {

            /*
             * IData param = new DataMap(); param.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER"));
             * param.put("TRADE_FEE", ""+cardFee); param.put("CHANNEL_ID", channelId); param.put("PAYMENT_ID",
             * paymentId); param.put("PAY_FEE_MODE_CODE", payFeeModeCode); IDataset ids = new DatasetList();
             */

            String serialNumber = inData.getString("SERIAL_NUMBER");
            String tradeId = inData.getString("TRADE_ID");
            String channelId = inData.getString("CHANNEL_ID", "15002");
            String paymentId = inData.getString("PAYMENT_ID", "1");
            String payFeeModeCode = inData.getString("PAY_FEE_MODE_CODE", "0");
            String remark = inData.getString("REMARK", "");
            // IDataset ids = tux TAM_RECV_FEE TUX接口充值 !@#
            // ids = CSAppCall.callAcct("AM_CRM_TransFee", param, false).getData();
            IDataset ids = AcctCall.recvFee(serialNumber, tradeId, "" + cardFee, channelId, paymentId, payFeeModeCode, remark);

            if (DataSetUtils.isBlank(ids))
            {
                // 调用成功，记录兑换日志
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口给用户充值失败");
            }
        }
    }

    /**
     * 兑换前校验兑换日志
     * 
     * @description
     * @throws Exception
     * @date 2014-3-29
     */
    public IData checkExchangeLog(String serialNumber, String serialNumber_b, String card_no, String cardTypeCode, int paracode1, int paracode2) throws Exception
    {
        IData retData = new DataMap();

        int sendSmsCount = paracode1; // 发送短信号码当月允许发送兑奖短信次数
        int sucExcCount = paracode2;

        // ----判断发短信的号码用户当月发送短信次数达到60次--
        IDataset logDs = GGCardInfoQry.getSerialnumberSendSmsCounts(serialNumber_b);
        if (DataSetUtils.isNotBlank(logDs))
        {
            if (logDs.getData(0).getInt("SMS_COUNT", 0) >= sendSmsCount)
            {
                retData.put("X_RESULTCODE", "60");
                retData.put("X_RESULTINFO", "您输入的刮奖码次数已超过本月最高刮奖次数，本次兑奖不成功，欢迎下月参加活动！");
                retData.put("X_RSPTYPE", "2");
                retData.put("X_RSPCODE", "2998");
                return retData;
            }
        }

        // ----判断兑奖号码用户当月已中奖已达到30次--
        logDs = GGCardInfoQry.getSnExchangeSucCounts(serialNumber);
        if (DataSetUtils.isNotBlank(logDs))
        {
            if (logDs.getData(0).getInt("SUC_COUNT", 0) >= sucExcCount)
            {
                retData.put("X_RESULTCODE", "30");
                retData.put("X_RESULTINFO", serialNumber + "号码本月参加兑奖已达到30次，本次兑奖不成功，欢迎下月参加活动！");
                retData.put("X_RSPTYPE", "2");
                retData.put("X_RSPCODE", "2998");
                return retData;
            }
        }

        return retData;
    }

    /**
     * @description: 校验刮刮卡信息
     * @throws Exception
     * @date 2014-4-1
     */
    private IData checkGGCardInfo(String inCardNo, String inSerialNumber, String inSendSmsSn, String inCardTypeCode) throws Exception
    {
        IData retData = new DataMap();
        IData params = new DataMap();
        // ----判断刮奖码是否已过期--
        // 1、先判断卡号信息是否存在
        params.clear();
        // IDataset cardDs = new DatasetList();
        IDataset cardDs = GGCardInfoQry.getGGCardInfoByCardNo(inCardNo, inCardTypeCode); // 取消调用资源接口 ,表在CRM!@#

        if (IDataUtil.isEmpty(cardDs))
        {
            retData.clear();
            retData.put("X_RESULTCODE", "101");
            retData.put("X_RESULTINFO", "您输入的刮奖码无效，本次兑奖不成功！");
            retData.put("X_RSPTYPE", "2");
            retData.put("X_RSPCODE", "2998");
            return retData;
        }
        // 2、判断该卡是否已兑过奖
        IData cardData = cardDs.getData(0);
        String cardStateCode = cardData.getString("CARD_STATE_CODE", "");
        String changeTime = cardData.getString("CHANGE_TIME", "");
        String endDate = cardData.getString("END_DATE", "");
        // 0-未兑奖， 1-已兑奖
        if ("1".equals(cardStateCode))
        {
            retData.clear();
            String month = getStringDateMonth(changeTime);
            String day = getStringDateDay(changeTime);
            retData.put("X_RESULTCODE", "99");
            retData.put("X_RESULTINFO", "您输入的刮奖码已于" + month + "月" + day + "日兑换过话费，本次兑奖不成功！");
            retData.put("MONTH", month); // 月
            retData.put("DAY", day); // 日
            retData.put("CHANGE_TIME", changeTime); // 兑换时间
            retData.put("X_RSPTYPE", "2");
            retData.put("X_RSPCODE", "2998");
            return retData;
        }
        // 3、判断卡号是否已过期
        params.clear();

        cardDs = GGCardInfoQry.getValidGGCardInfoByCardNo(inCardNo); // 取消获取有效的刮刮卡接口 表在CRM!@#
        if (cardDs == null || cardDs.size() == 0)
        {
            retData.clear();
            String month = getStringDateMonth(endDate);
            String day = getStringDateDay(endDate);
            retData.put("X_RESULTCODE", "100");
            retData.put("X_RESULTINFO", "您输入的刮奖码已于" + month + "月" + day + "日过期，本次兑奖不成功！");
            retData.put("MONTH", month); // 月
            retData.put("DAY", day); // 日
            retData.put("X_RSPTYPE", "2");
            retData.put("X_RSPCODE", "2998");
            return retData;
        }

        // ------------所有校验都通过则说明中奖----------
        cardData = cardDs.getData(0);
        cardStateCode = cardData.getString("CARD_STATE_CODE", "");
        if ("0".equals(cardStateCode))
        {
            retData.putAll(cardData);
            retData.put("X_RESULTCODE", "0");
        }
        else
        {
            retData.putAll(cardData);
            retData.put("X_RESULTCODE", "-1");
            retData.put("X_RESULTINFO", "不中奖");
            retData.put("X_RSPTYPE", "2");
            retData.put("X_RSPCODE", "2998");
        }

        return retData;
    }

    /**
     * 刮刮卡兑奖
     * 
     * @description
     * @date 2014-6-29
     */
    public IData ggCardExchange(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "SERIAL_NUMBER_B");
        IDataUtil.chkParam(data, "CARD_TYPE_CODE");
        IDataUtil.chkParam(data, "CARD_NO");
        IDataUtil.chkParam(data, "PARA_CODE1");
        IDataUtil.chkParam(data, "PARA_CODE2");
        IDataUtil.chkParam(data, "CHANNEL_ID");
        IDataUtil.chkParam(data, "PAYMENT_ID");
        IDataUtil.chkParam(data, "PAY_FEE_MODE_CODE");

        String serialNumber = data.getString("SERIAL_NUMBER");
        String serialNumber_b = data.getString("SERIAL_NUMBER_B");
        String cardTypecode = data.getString("CARD_TYPE_CODE");
        String cardNo = data.getString("CARD_NO");
        int paracode1 = data.getInt("PARA_CODE1");
        int paracode2 = data.getInt("PARA_CODE2");

        IData userinfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到用户信息！");
        }

        IData result = checkExchangeLog(serialNumber, serialNumber_b, cardNo, cardTypecode, paracode1, paracode2);
        if (IDataUtil.isNotEmpty(result))
        {
            return result;
        }
        // -------校验通过则做登记处理----

        // ----校验刮刮卡信息-------
        IData retMap = checkGGCardInfo(cardNo, serialNumber, serialNumber_b, cardTypecode);
        /**
         * 不管中奖与否都要记录兑奖日志
         */
        // ----中奖----
        String tradeId = SeqMgr.getTradeId();
        data.put("TRADE_ID", tradeId);
        if ("0".equals(retMap.getString("X_RESULTCODE", "")))
        {
            // ---------------记录业务受理台账------------------
            this.recordHisMainTrade(data, tradeId, userinfo);
            // ---------------修改刮刮卡状态--------------------
            IData updParams = new DataMap();
            updParams.put("CHANGE_SERIAL_NUMBER", serialNumber);
            updParams.put("CARD_NO", cardNo);
            updParams.put("CARD_STATE_CODE", "1");
            Dao.executeUpdateByCodeCode("TF_R_KKKL_GGCARD", "UPD_SUCEXCHNAGE_GGCARD", updParams);
            // -------记录兑奖日志------------
            this.insertExchangeLog(data, "1", tradeId);
            // ---调用账务充值接口---
            callActIntfPayFee(data, retMap.getInt("CARD_FEE", 0));
        }
        // ----不中奖也要记录兑换日志-----
        else
        {
            // -------记录兑奖日志------------
            this.insertExchangeLog(data, "0", tradeId); // 兑奖结果，0：未成功，1：成功
        }

        retMap.put("TRADE_ID", tradeId);
        return retMap;
    }

    /**
     * 记录兑换日志
     * 
     * @description
     */
    private void insertExchangeLog(IData inData, String changeResultCode, String tradeId) throws Exception
    {

        String cardNo = inData.getString("CARD_NO", ""); // 卡号
        String serialNumber = inData.getString("SERIAL_NUMBER", ""); // 兑奖号码
        String sendSmsSn = inData.getString("SERIAL_NUMBER_B", ""); // 发送短信号码
        String cardTypeCode = inData.getString("CARD_TYPE_CODE", ""); // 卡类型

        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("TRADE_TYPE_CODE", "429");
        params.put("IN_MODE_CODE", getVisit().getInModeCode());
        params.put("SERIAL_NUMBER", sendSmsSn);
        params.put("CHANGE_SERIAL_NUMBER", serialNumber);
        params.put("CARD_KIND_CODE", cardTypeCode);
        params.put("CARD_NO", cardNo);
        params.put("CHANGE_RESULT_CODE", changeResultCode);
        params.put("REMARK", "");
        params.put("RSRV_NUM1", "");
        params.put("RSRV_NUM2", "");
        params.put("RSRV_NUM3", "");
        params.put("RSRV_STR1", "");
        params.put("RSRV_STR2", "");
        params.put("RSRV_STR3", "");
        params.put("RSRV_STR4", "");
        params.put("RSRV_STR5", "");
        params.put("RSRV_DATE1", "");
        params.put("RSRV_DATE2", "");
        params.put("RSRV_DATE3", "");
        params.put("RSRV_TAG1", "");
        params.put("RSRV_TAG2", "");
        params.put("RSRV_TAG3", "");

        Dao.executeUpdateByCodeCode("TF_B_CHANGETRADE", "INS_EXCHANGE_LOG", params);
    }

    /**
     * 记录业务受理历史台账
     * 
     * @description
     * @date 2014-4-14
     */
    protected void recordHisMainTrade(IData data, String tradeId, IData userinfo) throws Exception
    {

        IData iparam = new DataMap();
        String orderId = SeqMgr.getOrderId();
        String priority = UTradeTypeInfoQry.getTradePri("429");
        // IData userinfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        /*
         * if(IDataUtil.isNotEmpty(userinfo)){ IData custinfo =
         * UcaInfoQry.qryCustInfoByCustId(userinfo.getString("CUST_ID")); IData acctinfo =
         * UcaInfoQry.qryAcctInfoByUserId(userinfo.getString("USER_ID")); if(IDataUtil.isNotEmpty(userinfo) ||
         * IDataUtil.isNotEmpty(userinfo)) }
         */

        iparam.put("TRADE_ID", tradeId);
        iparam.put("BATCH_ID", data.getString("BATCH_ID"));
        iparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        iparam.put("ORDER_ID", orderId);
        iparam.put("PROD_ORDER_ID", "");
        iparam.put("BPM_ID", "");
        iparam.put("CAMPN_ID", "");
        iparam.put("TRADE_TYPE_CODE", "429");
        iparam.put("PRIORITY", priority);
        iparam.put("SUBSCRIBE_TYPE", "0");
        iparam.put("SUBSCRIBE_STATE", "9");
        iparam.put("NEXT_DEAL_TAG", "0");
        iparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        iparam.put("USER_ID", userinfo.getString("USER_ID", ""));
        iparam.put("CUST_NAME", userinfo.getString("CUST_NAME", ""));
        iparam.put("ACCT_ID", userinfo.getString("ACCT_ID", ""));
        iparam.put("CUST_ID", userinfo.getString("CUST_ID", ""));
        iparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        iparam.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", "0"));
        iparam.put("EPARCHY_CODE", userinfo.getString("EPARCHY_CODE", ""));
        iparam.put("CITY_CODE", userinfo.getString("CITY_CODE", ""));
        iparam.put("PRODUCT_ID", userinfo.getString("PRODUCT_ID"));
        iparam.put("BRAND_CODE", userinfo.getString("BRAND_CODE"));
        iparam.put("CUST_ID_B", "");
        iparam.put("ACCT_ID_B", "");
        iparam.put("USER_ID_B", "");
        iparam.put("SERIAL_NUMBER_B", "");
        iparam.put("CUST_CONTACT_ID", "");
        iparam.put("SERV_REQ_ID", "");
        iparam.put("INTF_ID", "");
        iparam.put("ACCEPT_DATE", SysDateMgr.getSysDate());
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
        iparam.put("FINISH_DATE", SysDateMgr.getSysDate());
        iparam.put("EXEC_TIME", SysDateMgr.getSysDate());
        iparam.put("EXEC_ACTION", "0");
        iparam.put("EXEC_RESULT", "");
        iparam.put("EXEC_DESC", "");
        iparam.put("CANCEL_TAG", "0");
        iparam.put("CANCEL_DATE", "");
        iparam.put("CANCEL_STAFF_ID", "");
        iparam.put("CANCEL_DEPART_ID", "");
        iparam.put("CANCEL_CITY_CODE", "");
        iparam.put("CANCEL_EPARCHY_CODE", "");
        iparam.put("UPDATE_TIME", SysDateMgr.getSysDate());
        iparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        iparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        iparam.put("REMARK", "可口可乐刮刮卡兑奖成功！"); // 备注
        iparam.put("RSRV_STR1", data.getString("SERIAL_NUMBER_B", ""));
        iparam.put("RSRV_STR2", data.getString("CARD_TYPE_CODE", ""));
        iparam.put("RSRV_STR3", data.getString("CARD_NO", ""));
        iparam.put("RSRV_STR4", data.getString("PARA_CODE1", ""));
        iparam.put("RSRV_STR5", data.getString("PARA_CODE2", ""));
        iparam.put("RSRV_STR6", data.getString("CHANNEL_ID", ""));
        iparam.put("RSRV_STR7", data.getString("PAYMENT_ID", ""));
        iparam.put("RSRV_STR8", data.getString("PAY_FEE_MODE_CODE", ""));
        iparam.put("RSRV_STR9", "");
        iparam.put("RSRV_STR10", "");

        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_ALL", iparam);
    }
}
