
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.orderPre;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

public class GrpOrderPreBean
{

    public static IData dealOrderPreData(String tradeTypeCode, int amount, IData preOderData) throws Exception
    {
        if (IDataUtil.isEmpty(preOderData))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_53);
        }

        setPreOrderStaffInfos(preOderData);// 设置操作员信息
        String request_id = SeqMgr.getPreSmsSendId();
        String start_date = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
        String acceptMonth = StrUtil.getAcceptMonthById(request_id);// 取request_id中的月份，方便后面回复的时候取accept_month

        if (0 == amount)
        {// amount==0则为默认值48
            amount = 48;
        }
        Date end_Date = DateUtils.addHours(SysDateMgr.string2Date(start_date, SysDateMgr.PATTERN_STAND), amount);
        String end_DateString = SysDateMgr.date2String(end_Date, SysDateMgr.PATTERN_STAND);

        IData insertData = new DataMap();

        String pre_order_id = SeqMgr.getOrderId();
        insertData.put("PRE_ID", pre_order_id);
        insertData.put("PRE_TYPE", preOderData.getString("PRE_TYPE"));// 集团业务预约类型

        // 返回
        IData returnData = new DataMap();

        returnData.put("REQUEST_ID", request_id);
        insertData.put("ACCEPT_MONTH", acceptMonth);
        insertData.put("TRADE_TYPE_CODE", tradeTypeCode);
        insertData.put("START_DATE", start_date);
        insertData.put("END_DATE", end_DateString);
        insertData.put("ACCEPT_STATE", "0"); // 0是初始化状态

        // 先取tradeData里面的trade_id，没值则取空
        insertData.put("ORDER_ID", preOderData.getString("ORDER_ID", ""));
        insertData.put("SERIAL_NUMBER", preOderData.getString("SERIAL_NUMBER"));
        insertData.put("SVC_NAME", preOderData.getString("SVC_NAME", ""));

        String tmp = preOderData.toString();

        // 分割订购数据
        IDataset acceptDataset = StrUtil.StringSubsection(tmp, 2000);

        if (acceptDataset.size() > 5)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "订购数据字符串太长!");
        }

        // 订购数据, 分割插入ACCEPT_DATA0到ACCEPT_DATA5
        for (int i = 0, size = acceptDataset.size(); i < size; i++)
        {
            insertData.put("ACCEPT_DATA" + (i + 1), acceptDataset.get(i));
        }

        // 二次短信确认
        Dao.insert("TF_B_ORDER_PRE", insertData, Route.getJourDb(Route.CONN_CRM_CG));

        return returnData;
    }

    // 判断是否预约回调
    public static boolean isOrderPre2Back(IData ivkParam) throws Exception
    {
        boolean orderPreFlag = false;

        // ORDER_PRE为true IS_CONFIRM有只为预约回调
        if (ivkParam.getBoolean("ORDER_PRE") && ivkParam.getBoolean("IS_CONFIRM"))// IS_CONFIRM这个字段有带确认
        {

            IDataUtil.chkParam(ivkParam, "REQUEST_ID");

            IData oldDataMap = TwoCheckSms.queryDataMapByRequestId(ivkParam.getString("REQUEST_ID"));

            ivkParam.putAll(oldDataMap);

            orderPreFlag = false;
        }

        return orderPreFlag;
    }

    /**
     * 设置操作员信息
     * 
     * @param preOderData
     * @throws Exception
     */
    private static void setPreOrderStaffInfos(IData preOderData) throws Exception
    {
        if (StringUtils.isBlank(preOderData.getString("STAFF_ID", "")))
        {
            preOderData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        }

        if (StringUtils.isBlank(preOderData.getString("STAFF_NAME", "")))
        {
            preOderData.put("STAFF_NAME", CSBizBean.getVisit().getStaffName());
        }

        if (StringUtils.isBlank(preOderData.getString("DEPART_ID", "")))
        {
            preOderData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        }

        if (StringUtils.isBlank(preOderData.getString("DEPART_NAME", "")))
        {
            preOderData.put("DEPART_NAME", CSBizBean.getVisit().getDepartName());
        }
        if (StringUtils.isBlank(preOderData.getString("DEPART_CODE", "")))
        {
            preOderData.put("DEPART_CODE", CSBizBean.getVisit().getDepartCode());
        }

        if (StringUtils.isBlank(preOderData.getString("CITY_CODE", "")))
        {
            preOderData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        }
        if (StringUtils.isBlank(preOderData.getString("CITY_NAME", "")))
        {
            preOderData.put("CITY_NAME", CSBizBean.getVisit().getCityName());
        }
        if (StringUtils.isBlank(preOderData.getString("IN_MODE_CODE", "")))
        {
            preOderData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        }
        if (StringUtils.isBlank(preOderData.getString("STAFF_EPARCHY_CODE", "")))
        {
            preOderData.put("STAFF_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        }
        if (StringUtils.isBlank(preOderData.getString("TRADE_EPARCHY_CODE", "")))
        {
            preOderData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        }
    }
}
