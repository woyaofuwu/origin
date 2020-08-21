
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryvipexchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class QueryVipExchangeBean extends CSBizBean
{

    public IDataset queryVipExchange(IData data, Pagination pagination) throws Exception
    {
        String serialNum = data.getString("SERIAL_NUMBER", "");
        String updateTime = data.getString("UPDATE_TIME", "");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNum);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }

        String userId = userInfo.getString("USER_ID");

        IDataset exchanges = UserOtherInfoQry.getVipExchangeBySnUpdateTime(userId, serialNum, updateTime, pagination);

        for (int i = 0; i < exchanges.size(); i++)
        {
            IData exchange = exchanges.getData(i);
            String vipTypeCode = exchange.getString("VIP_TYPE_CODE", "");
            String vipClassId = exchange.getString("VIP_CLASS_ID", "");
            String isSendSms = exchange.getString("IS_SEND_SMS", "");
            String className = "";
            if (vipTypeCode.trim().length() != 0 && vipClassId.trim().length() != 0)
            {
                className = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipTypeCode, vipClassId);
            }
            exchange.put("CLASS_NAME", className);
            String sendSms = "否";
            if (StringUtils.equals("0", isSendSms))
            {
                sendSms = "否";
            }
            else if (StringUtils.equals("1", isSendSms))
            {
                sendSms = "是";
            }
            exchange.put("SEND_SMS", sendSms);
        }
        return exchanges;
    }

}
