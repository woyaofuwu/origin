
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class SmsSendSVC extends CSBizService
{

    private static final long serialVersionUID = 5298431900237559556L;

    public static IData insertSms(IData iData) throws Exception
    {
        String serial_number = IDataUtil.chkParam(iData, "SERIAL_NUMBER");
        String notice_content = IDataUtil.chkParam(iData, "NOTICE_CONTENT");

        iData.put("RECV_OBJECT", serial_number);
        iData.put("NOTICE_CONTENT", notice_content);

        SmsSend.insSms(iData);

        IData rsData = new DataMap();
        rsData.put("RESULT_INFO", "短信处理成功");

        return rsData;
    }

    public IData sendMes(IData data) throws Exception
    {
        String serial_number = data.getString("RECV_OBJECT");
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serial_number);
        inparam.put("REMOVE_TAG", "0");
        inparam.put("NET_TYPE_CODE", "00");
        IDataset users = UserInfoQry.getUserInfoBySerialNumber(serial_number, "0", "00");
        if (IDataUtil.isEmpty(users))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码无用户信息");
        }
        IData user = users.getData(0);

        String content = data.getString("NOTICE_CONTENT");
        String seqId = SeqMgr.getSmsSendId();
        String systime = SysDateMgr.getSysTime();
        String referTime = data.getString("REFER_TIME");
        String dealTime = data.getString("DEAL_TIME");
        if (StringUtils.isEmpty(referTime))
        {
            referTime = systime;
        }
        if (StringUtils.isEmpty(dealTime))
        {
            dealTime = systime;
        }
        inparam.put("SMS_NOTICE_ID", seqId);
        inparam.put("PARTITION_ID", seqId.substring(seqId.length() - 4));
        inparam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", "0898"));
        inparam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "6"));
        inparam.put("SMS_NET_TAG", "0");
        inparam.put("CHAN_ID", data.getString("CHAN_ID", "11"));// 短信渠道编码:客户服务
        inparam.put("SEND_OBJECT_CODE", "6");
        inparam.put("SEND_TIME_CODE", "1");
        inparam.put("SEND_COUNT_CODE", "1");
        inparam.put("RECV_OBJECT_TYPE", "00");// 被叫对象类型:00－手机号码
        inparam.put("RECV_OBJECT", serial_number);
        inparam.put("RECV_ID", user.getString("USER_ID"));// 被叫对象标识:传用户标识
        inparam.put("SMS_TYPE_CODE", "20");// 短信类型:20-业务通知
        inparam.put("SMS_KIND_CODE", "02");// 短信种类:02－短信通知
        inparam.put("NOTICE_CONTENT_TYPE", "0");// 短信内容类型:0－指定内容发送
        inparam.put("NOTICE_CONTENT", content);// content);
        inparam.put("REFERED_COUNT", "0");
        inparam.put("FORCE_REFER_COUNT", "1");// 指定发送次数
        inparam.put("FORCE_OBJECT", data.getString("FORCE_OBJECT", "10086"));
        inparam.put("FORCE_START_TIME", data.getString("FORCE_START_TIME", ""));
        inparam.put("SMS_PRIORITY", 1000);// 短信优先级
        inparam.put("REFER_TIME", referTime);// 提交时间
        inparam.put("REFER_STAFF_ID", data.getString("REFER_STAFF_ID", ""));// 提交员工
        inparam.put("REFER_DEPART_ID", data.getString("REFER_DEPART_ID", ""));// 提交部门
        inparam.put("DEAL_TIME", dealTime);// 处理时间
        inparam.put("DEAL_STATE", "15");// 处理状态:0－未处理
        inparam.put("REMARK", data.getString("REMARK", "终端平台短信通知"));
        inparam.put("MONTH", SysDateMgr.getCurMonth());
        inparam.put("DAY", SysDateMgr.getCurDay());

        return SmsSendSVC.insertSms(inparam);
    }

    public void setTrans(IData input) throws Exception
    {
        if (!"".equals(input.getString("SERIAL_NUMBER", "")))
        {
            return;
        }
        else if (!"".equals(input.getString("RECV_OBJECT", "")))
        {
            input.put("SERIAL_NUMBER", input.getString("RECV_OBJECT"));
            return;
        }
    }

}
