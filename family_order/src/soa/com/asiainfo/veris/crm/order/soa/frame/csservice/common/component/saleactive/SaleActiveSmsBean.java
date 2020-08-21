
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import java.util.Random;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserValidCodeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class SaleActiveSmsBean extends CSBizBean
{
    public IData sendVeriCodeSms(String serialNumber, String userId, String productId, String packageId, String noticeContent, int limitCount, String eparchyCode) throws Exception
    {
        int sendCount = 0;
        boolean isFirstSend = true;

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++)
        {
            sb.append(random.nextInt(10));
        }

        String smsCode = sb.toString();

        IData paraminfo = new DataMap();
        paraminfo.put("SERIAL_NUMBER", serialNumber);
        paraminfo.put("PRODUCT_ID", productId);

        IData volidinfo = UserValidCodeInfoQry.getUserValidCode(userId, serialNumber, productId, eparchyCode);

        if (IDataUtil.isNotEmpty(volidinfo))
        {
            sendCount = volidinfo.getInt("CHECK_COUNT", 0);
            isFirstSend = false;
        }

        if (limitCount <= sendCount)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您办理该营销活动业务验证码输入错误次数超过" + limitCount + "次，请明天来办理!");
        }

        int nowCount = sendCount + 1;

        if (sendCount == 0 && isFirstSend)
        {
            UserValidCodeInfoQry.insertUserValidCode(serialNumber, userId, productId, packageId, smsCode, eparchyCode);
        }
        else
        {
            UserValidCodeInfoQry.updateUserValidCode(serialNumber, userId, productId, smsCode, nowCount, eparchyCode);
        }

        noticeContent = noticeContent + "第" + nowCount + "次验证码 " + smsCode + "中国移动。";

        IData paramSms = new DataMap();
        paramSms.put("RECV_OBJECT", serialNumber);
        paramSms.put("NOTICE_CONTENT", noticeContent);
        SmsSend.insSms(paramSms);

        IData returnData = new DataMap();
        returnData.put("SMS_CODE", smsCode);
        returnData.put("CHECK_COUNT", nowCount);
        return returnData;
    }

    public void updVeriCodeOk(String serialNumber, String userId, String productId, String smsCode, String eparchyCode) throws Exception
    {
        UserValidCodeInfoQry.updateUserValidCode(serialNumber, userId, productId, smsCode, 0, eparchyCode);
    }
}
