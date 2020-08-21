
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.database.config.DBRouteCfg;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SmsException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.SecSmsTypeEnum;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSmsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeTwoCheckQry;

public class TwoCheckSms
{

    private final static Logger logger = Logger.getLogger(TwoCheckSms.class);

    /**
     * 调用业务数据： 注：由于原来的数据存在资源预占的情况，所以在调用服务的时候，需要将员工等信息设置为原来的
     * 
     * @param busiData
     * @return
     * @throws Exception
     */
    private static IDataset callBusiSvc(IData busiData) throws Exception
    {
        return CSAppCall.call(busiData.getString("SVC_NAME"), busiData);
    }

    /**
     * 校验是否调用受理服务
     * 
     * @return//smsType 二次短信类型 //ansContent 回复内容
     * @throws Exception
     */
    private static boolean checkIsCallSvc(String smsType, String ansContent) throws Exception
    {

        String configContent = "";
        if (StringUtils.equals(smsType, "PayRemind"))
        {
            configContent = SecSmsTypeEnum.PayRemind.getValue();
        }
        else if (StringUtils.equals(smsType, "PlatSvcSec"))
        {
            configContent = SecSmsTypeEnum.PlatSvcSec.getValue();
        }
        else if (StringUtils.equals(smsType, "SpecSvcSec"))
        {
            configContent = SecSmsTypeEnum.SpecSvcSec.getValue();
        }
        else if (StringUtils.equals(smsType, "WlanPreCard"))
        {
            configContent = SecSmsTypeEnum.WlanPreCard.getValue();
        }
        else if (StringUtils.equals(smsType, "EntityCard"))
        {
            configContent = SecSmsTypeEnum.EntityCard.getValue();
        }
        else if (StringUtils.equals(smsType, "ExpRemind"))
        {
            configContent = SecSmsTypeEnum.ExpRemind.getValue();
        }
        else if (StringUtils.equals(smsType, "OneCardMultiSnSec"))
        {
            configContent = SecSmsTypeEnum.OneCardMultiSnSec.getValue();
        }
        else if (StringUtils.equals(smsType, "GrpBussSec"))
        {
            configContent = SecSmsTypeEnum.GrpBussSec.getValue();
        }
        else if (StringUtils.equals(smsType, "MOSP_CANCEL"))
        {
            configContent = SecSmsTypeEnum.MOSP_CANCEL.getValue();
        }
        else if (StringUtils.equals(smsType, "ORDER_PLAT"))
        {
            configContent = SecSmsTypeEnum.ORDER_PLAT.getValue();
        }
        else
        {

        }

        if (StringUtils.isEmpty(configContent) || StringUtils.isEmpty(ansContent)) // 配置的未空，则代表任何情况都需要调用；如果未传回复内容，也默认要要调用
        {
            return true;
        }
        else
        {
            String[] configStrs = StringUtils.split(configContent, "|");
            for (int i = 0; i < configStrs.length; i++)
            {
                if (StringUtils.equals(configStrs[i], ansContent))
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 获取对应长度字符串
     * 
     * @param value
     * @param length
     * @return
     */
    private static final String getCharLengthStr(String value, int length)
    {
        if (StringUtils.isEmpty(value))
            return "";
        char chars[] = value.toCharArray();
        int charidx = 0;
        for (int charlen = 0; charlen < length && charidx < chars.length; charidx++)
        {
            if (chars[charidx] > '\200')
            {
                if ((charlen += 2) > length)
                    charidx--;
            }
            else
            {
                charlen++;
            }
        }
        return value.substring(0, charidx);
    }

    // 查询当前业务类型限制多久需要发送
    public static int getSucSmsLimitHour(String tradeTypeCode) throws Exception
    {

        int replyLimitHour = TradeCtrl.getCtrlInt(tradeTypeCode, TradeCtrl.CTRL_TYPE.SMS_TC_REPLY_LIMIT, 48);

        return replyLimitHour;
    }

    // 插入短信内容到TI_O_TWOCHECK_SMS
    private static boolean insertSms2ContentTable(IData iData) throws Exception
    {
        IDataUtil.chkParam(iData, "FLOW_ID");
        IDataUtil.chkParam(iData, "SERIAL_NUMBER");
        IDataUtil.chkParam(iData, "SMS_CONTENT");
        IDataUtil.chkParam(iData, "SMS_TYPE");
        IDataUtil.chkParam(iData, "OPR_SOURCE");// 老系统科创获取二次短信数据的接口中只获取 OPR_SOURCE= '1','2','8'的数据，
        // 告警短信 OPR_SOURCE = 2 此类短信是不需要回复的
        // 一级boss点播OPR_SOURCE = 0
        // 业务订购类：OPR_SOURCE = 1
        // 8=待补充

        iData.put("FLOW_ID", iData.getString("FLOW_ID"));

        String smsId = "";
        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {

            smsId = SeqMgr.getSeqSmsTwoCheckId();

        }
        else
        {

            smsId = SeqMgr.getSmsSendId();// 海南
        }
        iData.put("SMS_ID", smsId);
        iData.put("TRADE_ID", iData.getString("TRADE_ID", ""));// 用于一笔订单完成后需要发一个推荐业务的二次确认短信。
        iData.put("DEAL_STATE", "0");// 未处理
        iData.put("SERIAL_NUMBER", iData.getString("SERIAL_NUMBER"));// 手机号码

        iData.put("EXTEND_TAG", iData.getString("EXTEND_TAG", "1"));
        iData.put("SMS_CONTENT", iData.getString("SMS_CONTENT"));// 短信内容
        iData.put("TIMEOUT", iData.getString("TIMEOUT", ""));
        iData.put("UPDATE_TIME", iData.getString("UPDATE_TIME", SysDateMgr.getSysTime()));
        iData.put("INSERT_TIME", iData.getString("INSERT_TIME", SysDateMgr.getSysTime()));
        iData.put("EPARCHY_CODE", iData.getString("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()));
        iData.put("RSRV_STR1", iData.getString("RSRV_STR1", ""));
        iData.put("RSRV_STR2", iData.getString("RSRV_STR2", ""));
        iData.put("RSRV_STR4", iData.getString("RSRV_STR4", ""));
        iData.put("RSRV_STR5", iData.getString("RSRV_STR5", ""));
        iData.put("OPR_SOURCE", iData.getString("OPR_SOURCE"));
        iData.put("UPDATE_STAFF_ID", iData.getString("STAFF_ID", CSBizBean.getVisit().getStaffId()));
        iData.put("UPDATE_DEPART_ID", iData.getString("DEPART_ID", CSBizBean.getVisit().getDepartId()));
        iData.put("SP_CODE", iData.getString("SP_CODE", ""));
        iData.put("SP_NAME", iData.getString("SP_NAME", ""));
        iData.put("BIZ_CODE", iData.getString("BIZ_CODE", ""));
        iData.put("BIZ_NAME", iData.getString("BIZ_NAME", ""));
        iData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(iData.getString("FLOW_ID")));
        String smsType = iData.getString("SMS_TYPE");
        iData.put("SMS_TYPE", smsType);

        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            /**
             * 1--扣费提醒; 2--平台业务二次确认; 3--个人业务二次确认; 4--集团业务二次确认;5—wlan预售卡;6-实体卡;7-到期提醒- 短信类型分类: 扣费提醒-PayRemind 1;
             * 平台业务二次确认-PlatSvcSec 2; 特殊服务二次确认-SpecSvcSec 3; 影号 OneCardMultiSnSec 3 wlan预售卡-WlanPreCard 5;
             * 实体卡-EntityCard 6; 到期提醒-ExpRemind 7 集团业务二次确认-GrpBussSec 4 BofConst.中定义
             */

            // RSRV_STR3：这个字段科创那边要用的，不能丢掉
            // RSRV_STR3为2时，且短信内容包含 回复“是” 时，会校验用户的回复，必须是回复的“是 ”才调用接口。
            if (StringUtils.isNotBlank(iData.getString("RSRV_STR3")))// 也许别人前面就自己传了
            {
                iData.put("RSRV_STR3", iData.getString("RSRV_STR3"));
            }
            else
            {
                if (StringUtils.equals(smsType, "PayRemind"))
                {
                    iData.put("RSRV_STR3", "1");
                }
                else if (StringUtils.equals(smsType, "PlatSvcSec"))
                {
                    iData.put("RSRV_STR3", "2");
                }
                else if (StringUtils.equals(smsType, "SpecSvcSec"))
                {
                    iData.put("RSRV_STR3", "3");
                }
                else if (StringUtils.equals(smsType, "WlanPreCard"))
                {
                    iData.put("RSRV_STR3", "5");
                }
                else if (StringUtils.equals(smsType, "EntityCard"))
                {
                    iData.put("RSRV_STR3", "6");
                }
                else if (StringUtils.equals(smsType, "ExpRemind"))
                {
                    iData.put("RSRV_STR3", "7");
                }
                else if (StringUtils.equals(smsType, "OneCardMultiSnSec"))
                {
                    iData.put("RSRV_STR3", "3");
                }
                else if (StringUtils.equals(smsType, "GrpBussSec"))
                {
                    iData.put("RSRV_STR3", "4");
                }
            }
        }
        else
        {// 海南
            // 模板id放入预留字段3
            iData.put("RSRV_STR3", iData.getString("TEMPLATE_ID"));

        }
        return Dao.insert(TradeTableEnum.TRADE_TWOCHECK_SMS.getValue(), iData, Route.CONN_CRM_CEN);

    }

    public static boolean insertTwoCheck2BH(IData idata) throws Exception
    {

        String requestId = idata.getString("REQUEST_ID");

        IDataset ids = TradeTwoCheckQry.queryTwoCheckInfoByRequestId(requestId);

        if (IDataUtil.isEmpty(ids))
        {
            return false;
        }

        IData insertData = ids.getData(0);

        return Dao.insert("TF_BH_ORDER_PRE", insertData, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static Object invoker(String className, String functionName, Object[] objectGroup, Class[] classGroup) throws Exception
    {

        try
        {
            Class groupClass = Class.forName(className);
            Constructor cons = groupClass.getConstructor(new Class[]
            {});
            Object object = cons.newInstance(new Object[]
            {});
            java.lang.reflect.Method method = groupClass.getMethod(functionName, classGroup);

            return method.invoke(object, objectGroup);
        }
        catch (Exception e)
        {
            if (e.getMessage() != null)
            {
                String errorMsg = e.getMessage().toString();

                CSAppException.apperr(BizException.CRM_BIZ_5, errorMsg.replace("@", ""));
            }
            throw e;
        }
    }

    // 判断是否需要发送二次确认短信
    public static boolean isNeedSecSms(IData ivkParam) throws Exception
    {

        boolean secSmsFlag = true;
        // boolean sucSmsFlag=false;

        // 有外部流水号说明从外围接口短厅发起
        String isConfirm = ivkParam.getString("IS_CONFIRM");

        if ("true".equals(isConfirm))
        {

            IDataUtil.chkParam(ivkParam, "REQUEST_ID");

            IData oldDataMap = queryDataMapByRequestId(ivkParam.getString("REQUEST_ID"));

            ivkParam.putAll(oldDataMap);

            secSmsFlag = false;
        }

        return secSmsFlag;

    }

    /**
     * 二次短信回复，当不需要调用服务时的处理
     * 
     * @param smsType
     * @param serialNumber
     * @param orderPreData
     * @throws Exception
     */
    private static void notCallSvcDeal(String smsType, String serialNumber, IData orderPreData) throws Exception
    {
        if (StringUtils.equals(smsType, BofConst.SEC_TYPE_375))// 影号
        {
            String strSmsContent = "尊敬的客户，您好！您未成功订购中国移动的影号业务。如有疑问，请咨询10086。中国移动";
            sendFailSms(serialNumber, strSmsContent.toString());
        }
        else if (StringUtils.equals(smsType, BofConst.SPEC_SVC_SEC))// 个人特殊服务
        {
            String serviceName = "";
            String elementId = orderPreData.getString("ELEMENT_ID", "");
            if (StringUtils.isNotBlank(elementId))
            {
                serviceName = USvcInfoQry.getSvcNameBySvcId(elementId);
            }
            StringBuilder strSmsContent = new StringBuilder(100);
            strSmsContent.append("尊敬的客户，您好！您未成功订购中国移动的").append(serviceName).append("业务。如有疑问，请咨询10086。中国移动");
            sendFailSms(serialNumber, strSmsContent.toString());
        }else if(StringUtils.equals(orderPreData.getString("SVC_NAME"), "SS.ShareClusterFlowRegSVC.tradeReg")){
        	String strSmsContent = "您好，"+serialNumber+"拒绝了您加入流量共享群组，您可发送KTQZLLGX到10086开通群组流量共享，您可发送SQJRQZ+群组主账户手机号码到10086申请加入流量共享群组。您可以关注微信公众号“和4G-惠分享”，了解群组流量共享相关信息。中国移动";
            sendFailSms(orderPreData.getString("SERIAL_NUMBER"), strSmsContent.toString());
        }
    }

    public static IData queryDataMapByRequestId(String flowId) throws Exception
    {

        IDataset dataset = TradeTwoCheckQry.queryDataMapByRequestId(flowId);

        if (IDataUtil.isEmpty(dataset))
        {
            return new DataMap();
        }

        IData idataMap = dataset.getData(0);

        StringBuilder idataString = new StringBuilder();

        idataString.append(idataMap.getString("ACCEPT_DATA1", "")).append(idataMap.getString("ACCEPT_DATA2", "")).append(idataMap.getString("ACCEPT_DATA3", "")).append(idataMap.getString("ACCEPT_DATA4", "")).append(
                idataMap.getString("ACCEPT_DATA5", ""));

        String jsonObject = idataString.toString();

        IData iDataMap = new DataMap(jsonObject);

        iDataMap.put("PRE_TYPE", idataMap.getString("PRE_TYPE"));
        iDataMap.put("SVC_NAME", idataMap.getString("SVC_NAME"));// 服务名称，为必填
        iDataMap.put("RSRV_STR1", idataMap.getString("RSRV_STR1"));
        iDataMap.put("ACCEPT_STATE", idataMap.getString("ACCEPT_STATE"));

        return iDataMap;

    }

    // 获取短信模板
    public static IDataset queryTradeSmsTemplate(String tradeTypeCode, IData idata) throws Exception
    {

        String brandCode = idata.getString("BRAND_CODE", "ZZZZ");
        String productId = idata.getString("PRODUCT_ID", "-1");
        String cancelTag = idata.getString("CANCEL_TAG", "0");
        String epachyCode = idata.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        String inModeCode = idata.getString("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        String eventType = BofConst.SMS_SEC;
        // 查询模板
        IDataset ids = TradeSmsInfoQry.getTradeSmsInfos(tradeTypeCode, brandCode, productId, cancelTag, epachyCode, inModeCode, eventType);

        return ids;
    }

    /**
     * 发送失败短信
     * 
     * @throws Exception
     */
    private static void sendFailSms(String serialNumber, String strSmsContent) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("NOTICE_CONTENT", strSmsContent);
        inparam.put("IN_MODE_CODE", "0");
        inparam.put("RECV_OBJECT", serialNumber);
        inparam.put("RECV_ID", serialNumber);
        inparam.put("SMS_PRIORITY", "5000");
        inparam.put("REFER_STAFF_ID", "ITF00000");
        inparam.put("REFER_DEPART_ID", "ITF00");
        inparam.put("REMARK", "二次短信确认取消订购通知短信");
        SmsSend.insSms(inparam);
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

    /**
     * 二次确认短信执行
     * 
     * @param tradeTypeCode
     * @param amount
     * @param preOderData
     * @param smsData
     * @return
     * @throws Exception
     */
    public static IData twoCheck(String tradeTypeCode, int amount, IData preOderData, IData smsData) throws Exception
    {
        IData idata = null;
        String flowId = "";
        String extendTag = "";
        if (IDataUtil.isNotEmpty(preOderData))
        {
            if ("".equals(preOderData.getString("PRE_TYPE", "")))
            {
                preOderData.put("PRE_TYPE", smsData.getString("SMS_TYPE"));
            }
            idata = twoCheckPreOrder(tradeTypeCode, amount, preOderData);
            flowId = idata.getString("REQUEST_ID");

            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
            {
                extendTag = idata.getString("EXTEND_TAG");
            }

        }

        if (StringUtils.isEmpty(flowId))
        {
            flowId = SeqMgr.getPreSmsSendId();
        }

        if (IDataUtil.isNotEmpty(smsData))
        {

            smsData.put("FLOW_ID", flowId);
            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
            {
                smsData.put("EXTEND_TAG", extendTag);
            }
            insertSms2ContentTable(smsData);
        }

        IData resultData = new DataMap();
        resultData.put("REQUEST_ID", flowId);
        return resultData;
    }

    /**
     * 二次确认短信应答接口 1,查询TF_B_ORDER_PRE预受理表，得到入参和服务名x_trans_code 2,CSAppCall(服务名，入参),返回订单数据orderId
     * 3,回填orderId到TF_B_ORDER_PRE表
     * 
     * @param map
     * @return
     * @throws Exception
     */
    public static IDataset twoCheck2Back(IData map) throws Exception
    {

        if (logger.isDebugEnabled())
        {
            logger.debug("二次短信回复接口回传参数>>>>>>>>>>>>.：" + map.toString());
        }

        /******************** 参数检查 开始 ***************************/
        String requestId = map.getString("FORCE_OBJECT", map.getString("FLOW_ID"));// 检查流水是否存在
        String dealState = map.getString("DEAL_FLAG", "");// 调二次短信回复接口传过来的状态
        String serialNumber = map.getString("SERIAL_NUMBER", "");// 调二次短信回复接口传过来的号码
        String replyContent = map.getString("ANSWER_CONTENT");
        if (StringUtils.isBlank(requestId))
        {
            CSAppException.apperr(SmsException.CRM_SMS_3);
        }

        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(SmsException.CRM_SMS_6);
        }

        IData ordPreInfo = TwoCheckSms.queryDataMapByRequestId(requestId);// 根据流水检查对应的预受理工单是否存在
        if (IDataUtil.isEmpty(ordPreInfo))
        {
            CSAppException.apperr(SmsException.CRM_SMS_4, requestId);
        }

        if (!StringUtils.equals(ordPreInfo.getString("ACCEPT_STATE"), "0"))
        {
            CSAppException.apperr(SmsException.CRM_SMS_9, requestId);
        }
        
        String dataId = ordPreInfo.getString("RSRV_STR1");
        
        if("10086204".equals(dataId) || "10086209".equals(dataId))
        {
        	replyContent = map.getString("NOTICE_CONTENT").substring(7);
        }
        
        // if (StringUtils.isBlank(dealState))
        // {
        // CSAppException.apperr(SmsException.CRM_SMS_5);
        // }

        /*
         * if(!StringUtils.equals(dealState, "0")) { dealState = "11"; }
         */

        IDataset results = twoCheckReply(serialNumber, requestId, dealState, replyContent, map.getString("RSRV_STR1"));

        /** 非异常的情况下返回 **************/
        if (IDataUtil.isEmpty(results))
        {
            IData data = new DataMap();
            data.put("SERIAL_NUMBER", serialNumber);
            data.put("FLOW_ID", requestId);
            results.add(data);
        }
        else
        {
            results.getData(0).put("SERIAL_NUMBER", serialNumber);
            results.getData(0).put("FLOW_ID", requestId);
            
            /**
             * REQ201811290007++关于和多号0000查询及退订优化的需求
             * 加入副号码信息
             * mengqx 20190527
             */
            logger.error("results0527:"+results);
            if("3798".equals(results.getData(0).getString("ORDER_TYPE_CODE")) && !"".equals(results.getData(0).getString("TRADE_ID"))){
            	logger.error("ordPreInfo10527:"+ordPreInfo);
            	String serialNumberB = ordPreInfo.getString("SERIAL_NUMBER_B");
            	logger.error("acceptData10527:"+serialNumberB);
            	if(serialNumberB != null && !"".equals(serialNumberB)){
            		results.getData(0).put("SERIAL_NUMBER_B", serialNumberB);
            	}
            }
        }
        logger.error("results0527return:"+results);
        return results;
    }

    private static IData twoCheckPreOrder(String tradeTypeCode, int amount, IData preOderData) throws Exception
    {
        if (IDataUtil.isEmpty(preOderData))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_53);
            return new DataMap();
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
        insertData.put("PRE_TYPE", preOderData.getString("PRE_TYPE"));// 二次确认短信 类型

        //V网二次短信确认,从BOSS界面进来的,X_SUBTRANS_CODE为空,会捞取转换类,转换就有问题
        String productId = preOderData.getString("PRODUCT_ID","");
        String xSubTransCode = preOderData.getString("X_SUBTRANS_CODE","");
        if(StringUtils.isNotBlank(tradeTypeCode) && StringUtils.isNotBlank(productId) && 
        		"3034".equals(tradeTypeCode) && "8000".equals(productId))
        {
        	if(StringUtils.isBlank(xSubTransCode))
        	{
        		preOderData.put("X_SUBTRANS_CODE", "ProcessGrpVpmnMemTwoChk");
        	}
        }
        
        
        // 返回
        IData returnData = new DataMap();

        returnData.put("REQUEST_ID", request_id);

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {// 海南

            request_id = request_id.substring(request_id.length() - 8);

            String svcName = preOderData.getString("SVC_NAME", CSBizBean.getVisit().getXTransCode());
            String twoCheck3 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
            { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
            { "BMC_TWOCHECK_CODE3", svcName });

            insertData.put("RSRV_STR1", twoCheck3);

            if (StringUtils.isEmpty(twoCheck3))
                CSAppException.apperr(BizException.CRM_BIZ_11);

            request_id = twoCheck3 + request_id + acceptMonth;                       

            returnData.put("EXTEND_TAG", twoCheck3);
        }
        insertData.put("REQUEST_ID", request_id);
        insertData.put("ACCEPT_MONTH", acceptMonth);
        insertData.put("TRADE_TYPE_CODE", tradeTypeCode);
        insertData.put("START_DATE", start_date);
        insertData.put("END_DATE", end_DateString);
        insertData.put("ACCEPT_STATE", "0"); // 0是初始化状态

        // 先取tradeData里面的trade_id，没值则取空
        insertData.put("ORDER_ID", preOderData.getString("ORDER_ID", ""));
        insertData.put("SERIAL_NUMBER", preOderData.getString("SERIAL_NUMBER"));

        // 插入TF_B_ORDER_PRE表时 回复状态开始为0
        // insertData.put("REPLY_STATE", "0");

        insertData.put("SVC_NAME", preOderData.getString("SVC_NAME", ""));

        String tmp = preOderData.toString();

        // 分割订购数据
        IDataset acceptDataset = StrUtil.StringSubsection(tmp, 2000);

        if (acceptDataset.size() > 5)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "订购数据字符串太长!");
        }
        if("2582".equals(tradeTypeCode)){
        	 insertData.put("RSRV_STR2", "0");
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

    public static IDataset twoCheckReply(String serialNumber, String requestId, String dealState, String replyContent, String failDesc) throws Exception
    {
        IData ordPreInfo = TwoCheckSms.queryDataMapByRequestId(requestId);// 根据流水检查对应的预受理工单是否存在
        if (IDataUtil.isEmpty(ordPreInfo))
        {
            CSAppException.apperr(SmsException.CRM_SMS_4, requestId);
        }

        if (!StringUtils.equals(ordPreInfo.getString("ACCEPT_STATE"), "0"))
        {
            CSAppException.apperr(SmsException.CRM_SMS_9, requestId);
        }

        /******************** 参数检查 结束 ***************************/
        String smsType = ordPreInfo.getString("PRE_TYPE");
        ordPreInfo.remove("PRE_TYPE");// 为了不影响业务受理，删除

        // 更新预受理工单状态参数准备
        IData updParam = new DataMap();
        updParam.put("REQUEST_ID", requestId);
        updParam.put("REPLY_STATE", dealState);// 回复状态
        updParam.put("REPLY_TIME", SysDateMgr.getSysTime());// 回复时间
        updParam.put("REPLY_CONTENT", replyContent);// 回复内容

        // 取pre_order表里面的rsrv_str1,查询td_s_static,得到回复内容处理类,invoke
        String dataId = ordPreInfo.getString("RSRV_STR1");
        String invokeClass = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
        { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
        { "BMC_TWOCHECK_CODE3", dataId });

        if (StringUtils.isEmpty(invokeClass))
        {

            dealState = "-1"; // 默认不调用
            failDesc = "TD_S_STATIC中没有配置处理回复内容的类,请确认!";

        }
        else
        {

            IData backData = (IData) invoker(invokeClass, "actSmsBack", new Object[]
            { updParam }, new Class[]
            { IData.class });

            dealState = backData.getString("DEAL_STATE");
        }

        IDataset results = new DatasetList();
        if (StringUtils.equals("11", dealState))// 当回复状态为11时，才需要处理真正的业务受理
        {
            try
            {
                if (checkIsCallSvc(smsType, StringUtils.trim(replyContent)))
                {
                    ordPreInfo.put("REQUEST_ID", requestId);
                    ordPreInfo.put("REPLY_TIME", SysDateMgr.getSysTime());// 获取回复时间，没传则取系统时间
                    ordPreInfo.put("REPLY_CONTENT", replyContent);// 获取用户回复内容
                    ordPreInfo.put("IS_CONFIRM", "true");// 二次确认回复为true
                    //add by chenzg@20180608 二次确认回复短信办理，就不要处理上传附件了，否则会报：ORA-00001: 违反唯一约束条件 (UCR_CRM1.PK_TF_F_GROUP_FTPFILE)
                    if(ordPreInfo.containsKey("MEB_FILE_SHOW")){
                    	ordPreInfo.remove("MEB_FILE_SHOW");
                    }
                    if(ordPreInfo.containsKey("MEB_FILE_LIST")){
                    	ordPreInfo.remove("MEB_FILE_LIST");
                    }
                    results = callBusiSvc(ordPreInfo);// 调用具体业务对应的服务
                    if (IDataUtil.isNotEmpty(results))// 受理成功才填这些值
                    {
                        updParam.put("ORDER_ID", results.getData(0).getString("ORDER_ID", ""));// 业务受理产品的订单数据
                        updParam.put("ACCEPT_STATE", "9");
                        updParam.put("ACCEPT_RESULT", getCharLengthStr(results.toString(), 2000));// 防止超长
                    }
                }
                else
                {
                    // 不需要call服务时的处理
                    notCallSvcDeal(smsType, serialNumber, ordPreInfo);
                }
            }
            catch (Exception e)// 捕捉异常
            {
                updParam.put("ACCEPT_STATE", "-1");
                updParam.put("ACCEPT_RESULT", getCharLengthStr(Utility.getBottomException(e).getMessage(), 2000));// 防止超长
                throw e;
            }
            finally
            {
                updateOrderPre(updParam);
            }
        }
        else
        {
            // 直接更新预受理工单状态
            updParam.put("RSRV_STR5", failDesc);// 回复接口处理失败时填写失败原因--传入的
            updateOrderPre(updParam);
        }

        return results;
    }

    /**
     * 修改预受理订单
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    private static boolean updateOrderPre(IData idata) throws Exception
    {
        DBConnection conn = null;
        PreparedStatement stmt = null;
        String errorInfo = "";
        try
        {
            conn = new DBConnection(DBRouteCfg.getRoute(DBRouteCfg.getGroup(CSBizBean.getVisit().getSubSysCode()), Route.getJourDb(Route.CONN_CRM_CG)), true, false);
            StringBuilder parser = new StringBuilder();
            parser.append(" UPDATE TF_B_ORDER_PRE ");
            parser.append(" SET REPLY_STATE=?, ");
            parser.append(" REPLY_TIME= sysdate, ");
            parser.append(" REPLY_CONTENT=?, ");
            parser.append(" RSRV_STR5=?,");
            parser.append(" ORDER_ID=?,");
            parser.append(" ACCEPT_STATE=?, ");
            parser.append(" ACCEPT_RESULT=? ");
            parser.append(" WHERE REQUEST_ID =?");

            stmt = conn.prepareStatement(parser.toString());
            stmt.setString(1, idata.getString("REPLY_STATE", ""));
            stmt.setString(2, idata.getString("REPLY_CONTENT", ""));
            stmt.setString(3, idata.getString("RSRV_STR1", ""));
            stmt.setString(4, idata.getString("ORDER_ID", ""));
            stmt.setString(5, idata.getString("ACCEPT_STATE", "0"));
            stmt.setString(6, idata.getString("ACCEPT_RESULT", ""));
            stmt.setString(7, idata.getString("REQUEST_ID"));
            stmt.executeUpdate();
            conn.commit();
        }
        catch (Exception e)
        {
            if (null != conn)
            {
                conn.rollback();
            }

            Utility.print(e);
            errorInfo = Utility.getBottomException(e).getMessage();
            CSAppException.apperr(SmsException.CRM_SMS_8, errorInfo);
        }
        finally
        {
            if (null != stmt)
            {
                stmt.close();
            }

            if (null != conn)
            {
                conn.close();
            }
        }

        /*
         * if (StringUtils.isNotEmpty(errorInfo)) { CSAppException.apperr(SmsException.CRM_SMS_8, errorInfo); }
         */
        return true;
    }

    // 更新TF_B_ORDER_PRE表数据
    public static void updateTempData2TwoCheck(IDataset iDataset, IData ivkParam) throws Exception
    {

        IData inData = new DataMap();

        inData.put("REQUEST_ID", ivkParam.getString("REQUEST_ID"));
        inData.put("END_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        inData.put("REPLY_TIME", ivkParam.getString("REPLY_TIME"));
        inData.put("REPLY_CONTENT", ivkParam.getString("REPLY_CONTENT"));
        inData.put("REPLY_STATE", "1");

        if (IDataUtil.isNotEmpty(iDataset))
        {
            String orderId = iDataset.getData(0).getString("ORDER_ID", "");
            if ("".equals(orderId))
            {
                inData.put("ACCEPT_STATE", "F");
                inData.put("ACCEPT_RESULT", "回填ORDER失败");
            }
            else
            {
                inData.put("ORDER_ID", orderId);
                inData.put("ACCEPT_STATE", "9");
                inData.put("ACCEPT_RESULT", "回填ORDER成功");
            }
        }

        TwoCheckSms.updateTwoCheckByOutTradeId(inData);

    }

    /**
     * 更新TI_O_TWOCHECK_SMS表
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static int updateTwoCheckByFlowId(IData idata) throws Exception
    {

        StringBuilder parser = new StringBuilder();
        parser.append(" UPDATE TI_O_TWOCHECK_SMS ");
        parser.append("    SET DEAL_STATE        = :DEAL_STATE, ");
        parser.append("        ANSWER_CONTENT     = :ANSWER_CONTENT, ");
        parser.append("        RSRV_STR1     = :RSRV_STR1, ");
        parser.append("        UPDATE_TIME   =  sysdate");
        parser.append("  WHERE FLOW_ID = :FLOW_ID  ");
        parser.append("    AND SERIAL_NUMBER = :SERIAL_NUMBER ");

        return Dao.executeUpdate(parser, idata, Route.CONN_CRM_CEN);

    }

    public static boolean updateTwoCheckByOutTradeId(IData idata) throws Exception
    {

        String[] keys =
        { "REQUEST_ID" };

        return Dao.save("TF_B_ORDER_PRE", idata, keys, Route.getJourDb(Route.CONN_CRM_CG));
    }
}
