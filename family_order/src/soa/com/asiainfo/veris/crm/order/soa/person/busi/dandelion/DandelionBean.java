
package com.asiainfo.veris.crm.order.soa.person.busi.dandelion;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDandelionRecvQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class DandelionBean extends CSBizBean
{

    public static IData queryBizInfo(String bizTypeCode) throws Exception
    {
        if (bizTypeCode == null || bizTypeCode.trim().length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "推荐类型[" + bizTypeCode + "]值为空！");
        }
        IDataset paramDs = CommparaInfoQry.getCommpara("CSM", "1610", bizTypeCode, "0898");
        if (paramDs == null || paramDs.size() != 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取推荐类型[" + bizTypeCode + "]对应的推荐业务信息无数据！");
        }
        return paramDs.getData(0);
    }

    public void checkASerialNumberBizLimit(IData data, IData userInfoA, IData userInfoB, IData bizInfo) throws Exception
    {
        if (!userInfoA.containsKey("SERIAL_NUMBER"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "A号码业务校验：用户号码[SERIAL_NUMBER]是必须的！");
        }
        if (!userInfoA.containsKey("USER_ID"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "A号码业务校验：用户标识[USER_ID]是必须的！");
        }
        if (!userInfoB.containsKey("SERIAL_NUMBER"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "A号码业务校验：被推荐用户号码[SERIAL_NUMBER]是必须的！");
        }
        if (!userInfoB.containsKey("USER_ID"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "A号码业务校验：被推荐用户标识[USER_ID]是必须的！");
        }
        if (!bizInfo.containsKey("PARAM_CODE"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "A号码业务校验：推荐业务类型[PARAM_CODE]是必须的！");
        }

        String resultCode = "141414";
        try
        {
            String serialNumber = userInfoA.getString("SERIAL_NUMBER");
            String userId = userInfoA.getString("USER_ID");
            String serialNumberB = userInfoB.getString("SERIAL_NUMBER");
            String userIdB = userInfoB.getString("USER_ID");
            String bizTypeCode = bizInfo.getString("PARAM_CODE");

            String[] paramName =
            { "v_Serial_Number", "v_User_Id", "v_Serial_Number_b", "v_User_Id_b", "v_Biz_Type_Code", "v_Resultcode", "v_Resultinfo" };

            IData paramValue = new DataMap();
            paramValue.put("v_Serial_Number", serialNumber);// 推荐号码
            paramValue.put("v_User_Id", userId);// 推荐号码用户标识
            paramValue.put("v_Serial_Number_b", serialNumberB);// 被推荐号码
            paramValue.put("v_User_Id_b", userIdB);// 被推荐号码用户标识
            paramValue.put("v_Biz_Type_Code", bizTypeCode);// 推荐业务类型

            Dao.callProc("p_Csm_Dandelion_Checkphonea", paramName, paramValue);

            int iReusltCode = paramValue.getInt("v_Resultcode");
            String strResultInfo = paramValue.getString("v_Resultinfo");
            resultCode = String.valueOf(iReusltCode);
            if (iReusltCode != 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, strResultInfo);
            }
        }
        catch (Exception e)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultCode + ":" + e.getMessage());
        }
    }

    public void checkBSerialNumberBizLimit(IData data, IData userInfoA, IData userInfoB, IData bizInfo) throws Exception
    {
        if (!userInfoA.containsKey("SERIAL_NUMBER"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "B号码业务校验：用户号码[SERIAL_NUMBER]是必须的！");
        }
        if (!userInfoA.containsKey("USER_ID"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "B号码业务校验：用户标识[USER_ID]是必须的！");
        }
        if (!userInfoB.containsKey("SERIAL_NUMBER"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "B号码业务校验：被推荐用户号码[SERIAL_NUMBER]是必须的！");
        }
        if (!userInfoB.containsKey("USER_ID"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "B号码业务校验：被推荐用户标识[USER_ID]是必须的！");
        }
        if (!bizInfo.containsKey("PARAM_CODE"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "B号码业务校验：推荐业务类型[PARAM_CODE]是必须的！");
        }

        String resultCode = "141414";
        try
        {
            String serialNumber = userInfoA.getString("SERIAL_NUMBER");
            String userId = userInfoA.getString("USER_ID");
            String serialNumberB = userInfoB.getString("SERIAL_NUMBER");
            String userIdB = userInfoB.getString("USER_ID");
            String bizTypeCode = bizInfo.getString("PARAM_CODE");

            String[] paramName =
            { "v_Serial_Number", "v_User_Id", "v_Serial_Number_b", "v_User_Id_b", "v_Biz_Type_Code", "v_Resultcode", "v_Resultinfo" };

            IData paramValue = new DataMap();
            paramValue.put("v_Serial_Number", serialNumber);// 推荐号码
            paramValue.put("v_User_Id", userId);// 推荐号码用户标识
            paramValue.put("v_Serial_Number_b", serialNumberB);// 被推荐号码
            paramValue.put("v_User_Id_b", userIdB);// 被推荐号码用户标识
            paramValue.put("v_Biz_Type_Code", bizTypeCode);// 推荐业务类型

            Dao.callProc("p_Csm_Dandelion_Checkphoneb", paramName, paramValue);

            int iReusltCode = paramValue.getInt("v_Resultcode");
            String strResultInfo = paramValue.getString("v_Resultinfo");
            resultCode = String.valueOf(iReusltCode);
            if (iReusltCode != 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, strResultInfo);
            }
        }
        catch (Exception e)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultCode + ":" + e.getMessage());
        }
    }

    public IData dandelionRecvDeal(IData data) throws Exception
    {
        IData retMap = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER", "").trim(); // 回复号码（被推荐号码）
        String forceObject = data.getString("FORCE_OBJECT", "").trim(); // 推荐短信下发码
        String noticeContent = data.getString("NOTICE_CONTENT", "").trim(); // YES-办理、NO-不办理
        // ----------获取推荐短信信息-----------------
        IDataset messageDs = UserDandelionRecvQry.getRecvMessageInfo(data.getString("FORCE_OBJECT", ""), data.getString("SERIAL_NUMBER", ""));
        if (messageDs == null || messageDs.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据下发码[" + forceObject + "]获取下发推荐短信无数据！");
        }
        if (messageDs.size() != 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据下发码[" + forceObject + "]获取下发推荐短信数据超过1条！");
        }
        // ----------判断是否三天内回复----------------
        messageDs = UserDandelionRecvQry.getValidRecvMessageInfo(data.getString("FORCE_OBJECT", ""), data.getString("SERIAL_NUMBER", ""));
        if (messageDs == null || messageDs.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据下发码[" + forceObject + "]获取的推荐短信数据已超过3天(72小时)！");
        }
        IData messageData = messageDs.getData(0);
        // ---------获取推荐业务对应的具体业务类型--------
        String tradeId = messageData.getString("TRADE_ID", "").trim();
        String serialNumberA = messageData.getString("SERIAL_NUMBER", "").trim(); // 推荐业务的号码A
        String bizTypeCode = messageData.getString("BIZ_TYPE_CODE", "").trim();
        String bizCode = messageData.getString("BIZ_CODE", "").trim(); // 具体的优惠、服务编码
        IData bizInfo = queryBizInfo(bizTypeCode);
        String elementTypeCode = bizInfo.getString("PARA_CODE3", "").trim(); // 具体的业务类型：D-优惠，S-服务

        // ----------根据回复内容处理是否开通业务-------
        String stateCode = "0"; // 处理状态:0-原始状态、1-用户回复YES状态、2-用户回复NO状态、9-用户不回复状态
        String relationTradeId = "";
        /*
         * YES:开通 ，调用开通接口处理NO：不开通，修改推荐短信状态
         */
        if ("YES".equals(noticeContent))
        {
            /* 推荐的是优惠，调用产品变更接口 */
            if ("D".equals(elementTypeCode))
            {
                IData cpData = new DataMap();

                cpData.putAll(data);
                cpData.put("TRADE_TYPE_CODE", "110");
                cpData.put("ORDER_TYPE_CODE", "110");
                cpData.put("SERIAL_NUMBER", serialNumber);
                cpData.put("ELEMENT_TYPE_CODE", elementTypeCode);
                cpData.put("ELEMENT_ID", bizCode);
                cpData.put("MODIFY_TAG", "0");
                IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", cpData);
                stateCode = "1";
                relationTradeId = results.getData(0).getString("TRADE_ID", "");
                retMap = results.getData(0);

            }
        }
        /* 回复No，修改推荐短信状态 */
        else if ("NO".equals(noticeContent))
        {
            stateCode = "2"; // 2-用户回复NO状态
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "[NOTICE_CONTENT]=[" + noticeContent + "],必须是YES或NO");
        }

        // ----------修改推荐信息状态-----------------
        updateRecvMessage(tradeId, stateCode, relationTradeId);
        // ----------回传推荐号码给接口调用方----------
        retMap.put("SERIAL_NUMBER", serialNumberA); //
        // ----------提交数据事务---------------------

        return retMap;
    }

    public IData dandelionSendDeal(IData data) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER", "").trim();
        String serialNumberB = data.getString("SERIAL_NUMBER_B", "").trim();
        String bizTypeCode = data.getString("BIZ_TYPE_CODE", "").trim();
        String tradeId = SeqMgr.getDandeLionId();
        data.put("TRADE_ID", tradeId); // 推荐短信流水，不同于平常业务办理流水号
        // ----------校验推荐号码和被推荐号码是否一样--------
        if (serialNumber.equals(serialNumberB))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "推荐号码和被推荐号码不能相同！");
        }
        // ----------获取A号码和B号码的正常用户资料---------
        IData userInfoA = UcaInfoQry.qryUserInfoBySn(serialNumber);// DandelionBusiUtil.queryUserInfo(data,
        // serialNumber);
        IData userInfoB = UcaInfoQry.qryUserInfoBySn(serialNumberB);// DandelionBusiUtil.queryUserInfo(data,
        // serialNumberB);
        // ----------获取推荐业务类型对应的业务信息---------
        IData bizInfo = queryBizInfo(data, bizTypeCode);
        data.put("BIZ_NAME", bizInfo.getString("PARAM_NAME", ""));
        // ----------记录A号码发送的推荐短信------------
        recordSendMessage(data, userInfoA, userInfoB, bizInfo);
        // ----------A号码的推荐条件判断---------------
        checkASerialNumberBizLimit(data, userInfoA, userInfoB, bizInfo);
        // ----------B号码的推荐条件判断---------------
        checkBSerialNumberBizLimit(data, userInfoA, userInfoB, bizInfo);
        // ----------向B号码发送推荐短信---------------
        sendMessageToSerialNumberB(data, userInfoA, userInfoB, bizInfo);
        // ----------记录向B号码发送的推荐短信信息-------
        recordRecvMessage(data, userInfoA, userInfoB, bizInfo);

        return new DataMap();
    }

    public String decodeNoticeContent(IData data, IData userInfoA, IData userInfoB, IData bizInfo) throws Exception
    {
        String noticeContent = bizInfo.getString("PARA_CODE23", "") + bizInfo.getString("PARA_CODE24", "") + bizInfo.getString("PARA_CODE25", "");
        // 推荐号码解析
        if (noticeContent.contains("%SERIAL_NUMBER!"))
        {
            noticeContent = noticeContent.replaceAll("%SERIAL_NUMBER!", userInfoA.getString("SERIAL_NUMBER", ""));
        }
        // 推荐业务解析
        if (noticeContent.contains("%BIZ_NAME!"))
        {
            noticeContent = noticeContent.replaceAll("%BIZ_NAME!", bizInfo.getString("PARAM_NAME", ""));
        }

        if (noticeContent == null || noticeContent.trim().length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取向B用户发送的推荐短信内容为空！");
        }
        return noticeContent;
    }

    private IData queryBizInfo(IData data, String bizTypeCode) throws Exception
    {
        if (bizTypeCode == null || bizTypeCode.trim().length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "推荐类型[" + bizTypeCode + "]值为空！");
        }
        IDataset paramDs = ParamInfoQry.getCommparaByCode("CSM", "1610", bizTypeCode, "0898");
        if (paramDs == null || paramDs.size() != 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取推荐类型[" + bizTypeCode + "]对应的推荐业务信息无数据！");
        }
        return paramDs.getData(0);
    }

    public void recordRecvMessage(IData data, IData userInfoA, IData userInfoB, IData bizInfo) throws Exception
    {
        IData tabData = new DataMap();
        String tradeId = data.getString("TRADE_ID", "");
        String sysdate = SysDateMgr.getSysTime();
        tabData.put("TRADE_ID", tradeId); // 推荐流水号
        tabData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId)); // 推荐月份
        tabData.put("SERIAL_NUMBER", userInfoA.getString("SERIAL_NUMBER", "")); // 推荐用户号码
        tabData.put("USER_ID", userInfoA.getString("USER_ID", "")); // 推荐用户标识
        tabData.put("SERIAL_NUMBER_B", userInfoB.getString("SERIAL_NUMBER", "")); // 被推荐用户号码
        tabData.put("USER_ID_B", userInfoB.getString("USER_ID", "")); // 被推荐用户标识
        tabData.put("BIZ_TYPE_CODE", bizInfo.getString("PARAM_CODE", "")); // 推荐的业务类型
        tabData.put("BIZ_CODE", bizInfo.getString("PARA_CODE1", "")); // 推荐的业务编码：优惠编码、服务编码等
        String smsSeq = tradeId.substring(8);
        String forceObject = "10086" + "200" + smsSeq;// 下发通道
        tabData.put("FORCE_OBJECT", forceObject); // 推荐短信下发对象
        tabData.put("SEND_DATE", sysdate); // 推荐时间
        tabData.put("STATE_CODE", "0"); // 处理状态:0-原始状态、1-用户回复YES状态、2-用户回复NO状态、9-用户不回复状态
        tabData.put("SCORE_TAG", "0"); // 蒲公英计划赠送积分处理标志 0-未处理 1-已处理
        tabData.put("UPDATE_TIME", sysdate); // 更新时间
        tabData.put("UPDATE_STAFF_ID", getVisit().getStaffId()); // 更新工号
        tabData.put("UPDATE_DEPART_ID", getVisit().getDepartId()); // 更新工号部门
        tabData.put("REMARK", ""); // 备注
        tabData.put("RSRV_STR1", "");
        tabData.put("RSRV_STR2", "");
        tabData.put("RSRV_STR3", "");
        tabData.put("RSRV_STR4", "");
        tabData.put("RSRV_STR5", "");
        tabData.put("RSRV_DATE1", "");
        tabData.put("RSRV_DATE2", "");
        tabData.put("RSRV_DATE3", "");
        tabData.put("RSRV_TAG1", "");
        tabData.put("RSRV_TAG2", "");
        tabData.put("RSRV_TAG3", "");
        Dao.insert("TF_F_USER_DANDELION_RECV", tabData);
    }

    private void recordSendMessage(IData data, IData userInfoA, IData userInfoB, IData bizInfo) throws Exception
    {
        IData tabData = new DataMap();
        String tradeId = data.getString("TRADE_ID", "");
        String sysdate = SysDateMgr.getSysTime();
        tabData.put("TRADE_ID", tradeId); // 推荐流水号
        tabData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId)); // 推荐月份
        tabData.put("SERIAL_NUMBER", userInfoA.getString("SERIAL_NUMBER", "")); // 推荐用户号码
        tabData.put("USER_ID", userInfoA.getString("USER_ID", "")); // 推荐用户标识
        tabData.put("SERIAL_NUMBER_B", userInfoB.getString("SERIAL_NUMBER", "")); // 被推荐用户号码
        tabData.put("USER_ID_B", userInfoB.getString("USER_ID", "")); // 被推荐用户标识
        tabData.put("BIZ_TYPE_CODE", bizInfo.getString("PARAM_CODE", "")); // 推荐的业务类型
        tabData.put("BIZ_CODE", bizInfo.getString("PARA_CODE1", "")); // 推荐的业务编码：优惠编码、服务编码等
        tabData.put("SEND_DATE", sysdate); // 推荐时间
        tabData.put("UPDATE_TIME", sysdate); // 更新时间
        tabData.put("UPDATE_STAFF_ID", getVisit().getStaffId()); // 更新工号
        tabData.put("UPDATE_DEPART_ID", getVisit().getDepartId()); // 更新工号部门
        tabData.put("REMARK", ""); // 备注
        tabData.put("RSRV_STR1", "");
        tabData.put("RSRV_STR2", "");
        tabData.put("RSRV_STR3", "");
        tabData.put("RSRV_STR4", "");
        tabData.put("RSRV_STR5", "");
        tabData.put("RSRV_DATE1", "");
        tabData.put("RSRV_DATE2", "");
        tabData.put("RSRV_DATE3", "");
        tabData.put("RSRV_TAG1", "");
        tabData.put("RSRV_TAG2", "");
        tabData.put("RSRV_TAG3", "");
        Dao.insert("TF_F_USER_DANDELION_SEND", tabData);
    }

    public void sendMessageToSerialNumberB(IData data, IData userInfoA, IData userInfoB, IData bizInfo) throws Exception
    {
        String tradeId = data.getString("TRADE_ID", "");
        String smsSeq = tradeId.substring(8);
        String forceObject = "10086" + "200" + smsSeq;// 下发通道
        // 组织短信内容
        String noticeContent = decodeNoticeContent(data, userInfoA, userInfoB, bizInfo);
        String smsdNoticeId = SeqMgr.getSmsSendId();

        IData param = new DataMap();
        param.put("SMS_NOTICE_ID", smsdNoticeId);
        param.put("NOTICE_CONTENT", noticeContent);
        param.put("SERIAL_NUMBER", userInfoB.getString("SERIAL_NUMBER"));
        param.put("USER_ID", userInfoB.getString("USER_ID"));
        param.put("FORCE_OBJECT", forceObject);
        param.put("STAFF_ID", getVisit().getStaffId());
        param.put("DEPART_ID", getVisit().getDepartId());
        param.put("EPARCHY_CODE", "0898");
        param.put("BRAND_CODE", "");
        param.put("IN_MODE_CODE", "3");
        param.put("SMS_NET_TAG", "0");
        param.put("CHAN_ID", "C006");
        param.put("SEND_OBJECT_CODE", "6");
        param.put("SEND_TIME_CODE", "1");
        param.put("SEND_COUNT_CODE", "1");
        param.put("RECV_OBJECT_TYPE", "00");
        param.put("RECV_OBJECT", userInfoB.getString("SERIAL_NUMBER"));
        param.put("RECV_ID", userInfoB.getString("USER_ID"));
        param.put("SMS_TYPE_CODE", "20");
        param.put("SMS_KIND_CODE", "08");
        param.put("NOTICE_CONTENT_TYPE", "0");
        param.put("REFERED_COUNT", "0");
        param.put("FORCE_REFER_COUNT", "1");
        param.put("FORCE_OBJECT", forceObject);
        param.put("SMS_PRIORITY", "5000");
        param.put("REFER_TIME", SysDateMgr.getSysTime());
        param.put("REFER_STAFF_ID", getVisit().getStaffId());
        param.put("REFER_DEPART_ID", getVisit().getDepartId());
        param.put("DEAL_TIME", SysDateMgr.getSysTime());
        param.put("DEAL_STAFFID", "");
        param.put("DEAL_DEPARTID", "");
        param.put("DEAL_STATE", "15");
        param.put("REMARK", "");
        param.put("REVC1", "");
        param.put("REVC2", "");
        param.put("REVC3", "");
        param.put("REVC4", "");
        param.put("MONTH", SysDateMgr.getCurMonth());
        param.put("DAY", SysDateMgr.getCurDay());
        SmsSend.insSms(param);
    }

    public int updateRecvMessage(String tradeId, String stateCode, String relationTradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("STATE_CODE", stateCode);
        params.put("RELATION_TRADE_ID", relationTradeId);
        params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        return Dao.executeUpdateByCodeCode("TF_F_USER_DANDELION_RECV", "UPD_VALID_RECVMESSAGE_STATE", params);
    }

}
