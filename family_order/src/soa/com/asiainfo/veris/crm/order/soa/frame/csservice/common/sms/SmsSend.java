
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;

public final class SmsSend
{
    public static enum SMS_TYPE
    {
        PUSH_SMS("P"); // push短信,对应TD_B_TRADE_SMS中的sms_type字段,push短信需要调换发送人和接受人顺序

        private final String value;

        private SMS_TYPE(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    public static final int getCharLength(String value, int length)
    {
        char chars[] = value.toCharArray();
        int charidx = 0;
        for (int charlen = 0; charlen < length && charidx < chars.length; charidx++)
            if (chars[charidx] > '\200')
            {
                charlen += 2;
                if (charlen > length)
                {
                    charidx = charidx - 1;
                }
            }
            else
            {
                charlen++;
            }

        return charidx;
    }

    /**
     * 写短信表TI_O_SMS 原来执行TI_O_SMS-INS_SMSCO_CS，有些值是写死的，这里使用默认值
     * 
     * @param data
     * @throws Exception
     */
    public static void insSms(IData data) throws Exception
    {
        IData sendData = prepareSmsData(data);
        Dao.insert("TI_O_SMS", sendData);
    }
    
    
    /**
     * 根据路由插短信
     * 
     * @param data
     * @throws Exception
     */
    public static void insSms(IData data,String eparchyCode) throws Exception
    {
        IData sendData = prepareSmsData(data);
        Dao.insert("TI_O_SMS", sendData,eparchyCode);
    }

    /**
     * 准备短信数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData prepareSmsData(IData data) throws Exception
    {
        IData sendData = new DataMap();

        String sysdate = SysDateMgr.getSysTime();

        /*------------------------以下是原来需要传入的值--------------------------*/
        // 判断是否为空，如果空，则新生成
        String smsNoticeId = data.getString("SMS_NOTICE_ID", "");
        if (StringUtils.isBlank(smsNoticeId))
        {
            smsNoticeId = SeqMgr.getSmsSendId();
        }
        sendData.put("SMS_NOTICE_ID", smsNoticeId);

        sendData.put("PARTITION_ID", smsNoticeId.substring(smsNoticeId.length() - 4));
        sendData.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode()));

        String recvObject = data.getString("RECV_OBJECT");
        String forceObject = data.getString("FORCE_OBJECT", "10086");

        String smsType = data.getString("SMS_TYPE", "");
        // push短信需要调换发送人和接收人
        if (smsType.equals(SMS_TYPE.PUSH_SMS.getValue()))
        {
            recvObject = data.getString("FORCE_OBJECT", "10086");
            forceObject = data.getString("RECV_OBJECT");
        }

        sendData.put("RECV_OBJECT", recvObject);// 手机号（服务号）（集团客户经理）也可以扩展其他业务
        sendData.put("RECV_ID", data.getString("RECV_ID", "-1"));// 因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务

        // 短信截取
        String content = data.getString("NOTICE_CONTENT", "");
        // int charLength = Utility.getCharLength(content, 500);
        int charLength = getCharLength(content, 4000);
        content = content.substring(0, charLength);
        sendData.put("NOTICE_CONTENT", content);

        String tempateId = data.getString("TEMPLATE_ID");
        String smsTypeCode = "I0";
        String smsKindCode = "02";

        String smsPriority = "2000";
        if (StringUtils.isNotEmpty(tempateId))
        {
            IData templateIds = TemplateBean.getTemplateInfoByPk(tempateId);
            if (IDataUtil.isNotEmpty(templateIds))
            {
                smsTypeCode = templateIds.getString("TEMPLATE_TYPE");
                smsKindCode = templateIds.getString("TEMPLATE_KIND");
                smsPriority = templateIds.getString("SMS_PRIORITY", "2000");
            }
        }
        //二进制短信,两步一块需求特殊处理
        String isBin = data.getString("IS_BIN");
        if (StringUtils.equals(isBin,"1"))
        {
            smsKindCode = data.getString("SMS_KIND_CODE", "02");
            smsPriority = data.getString("SMS_PRIORITY", "2000");
        }

        /*------------------------以下是原来写死的值，改用默认值--------------------------*/
        sendData.put("SEND_COUNT_CODE", data.getString("SEND_COUNT_CODE", "1"));// 发送次数编码?
        sendData.put("REFERED_COUNT", data.getString("REFERED_COUNT", "0"));// 发送次数？
        sendData.put("CHAN_ID", data.getString("CHAN_ID", "11"));
        sendData.put("RECV_OBJECT_TYPE", data.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
        sendData.put("SMS_TYPE_CODE", smsTypeCode);// 20用户办理业务通知
        sendData.put("SMS_KIND_CODE", smsKindCode);// 02与SMS_TYPE_CODE配套
        sendData.put("NOTICE_CONTENT_TYPE", data.getString("NOTICE_CONTENT_TYPE", "0"));// 0指定内容发送
        sendData.put("FORCE_REFER_COUNT", data.getString("FORCE_REFER_COUNT", "1"));// 指定发送次数
        sendData.put("SMS_PRIORITY", smsPriority);// 短信优先级
        sendData.put("REFER_TIME", data.getString("REFER_TIME", sysdate));// 提交时间
        sendData.put("REFER_STAFF_ID", data.getString("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId()));// 员工ID
        sendData.put("REFER_DEPART_ID", data.getString("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId()));// 部门ID
        sendData.put("DEAL_TIME", data.getString("DEAL_TIME", sysdate));// 完成时间
        sendData.put("DEAL_STATE", data.getString("DEAL_STATE", "0"));// 处理状态，0：已处理，15未处理
        sendData.put("SEND_OBJECT_CODE", data.getString("SEND_OBJECT_CODE", "6"));// 通知短信,见TD_B_SENDOBJECT
        sendData.put("SEND_TIME_CODE", data.getString("SEND_TIME_CODE", "1"));// 营销时间限制,见TD_B_SENDTIME
        sendData.put("REMARK", data.getString("REMARK", ""));// 备注

        /*------------------------以下是原来没有写入的值--------------------------*/
        sendData.put("BRAND_CODE", data.getString("BRAND_CODE"));
        sendData.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode()));// 接入方式编码
        sendData.put("SMS_NET_TAG", data.getString("SMS_NET_TAG", "0"));
        sendData.put("FORCE_OBJECT", forceObject);// 发送方号码
        sendData.put("FORCE_START_TIME", data.getString("FORCE_START_TIME", ""));// 指定起始时间
        sendData.put("FORCE_END_TIME", data.getString("FORCE_END_TIME", ""));// 指定终止时间
        sendData.put("DEAL_STAFFID", data.getString("DEAL_STAFFID"));// 完成员工
        sendData.put("DEAL_DEPARTID", data.getString("DEAL_DEPARTID"));// 完成部门
        sendData.put("REVC1", data.getString("REVC1"));
        sendData.put("REVC2", data.getString("REVC2"));
        sendData.put("REVC3", data.getString("TEMPLATE_ID"));
        sendData.put("REVC4", data.getString("REVC4"));
        sendData.put("MONTH", sysdate.substring(5, 7));// 月份
        sendData.put("DAY", sysdate.substring(8, 10)); // 日期

        return sendData;
    }

    public static void twoCheck(IData params) throws Exception
    {
        Dao.insert("TF_B_TWO_CHECK", params);
    }

    public static void twoCheckSms(IData params) throws Exception
    {
        Dao.insert("TI_O_TWOCHECK_SMS", params, Route.CONN_CRM_CEN);
    }

}
