
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DealNotRealNameUserOfficeOpenStop.java
 * @Description: 于待激活号码未登记实名用户的办理销户或复机，给服开送局方停机或局开指令
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-16 上午10:14:15
 */
public class DealNotRealNameUserOfficeOpenStop implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        boolean isSendOfficeOpenStop = false;
        String userId = btd.getRD().getUca().getUserId();
        List<UserTradeData> userTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
        // 个人用户复机或无线固话复机完工时，对于待激活号码未登记实名用户的复机，则给用户发送局方停机指令
        UserTradeData userTradeData = null;
        if (userTradeDataList != null && userId.length() > 0)
        {
            for (int i = 0, count = userTradeDataList.size(); i < count; i++)
            {
                if (StringUtils.equals(userId, userTradeDataList.get(i).getUserId()))
                {
                    userTradeData = userTradeDataList.get(i);
                    break;
                }
            }
            String acctTag = "";
            acctTag = userTradeData.getAcctTag();
            String custId = userTradeData.getCustId();
            if (StringUtils.equals("2", acctTag))
            { // 待激活用户
                String isRealName = "";
                List<CustomerTradeData> customerTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER);
                if (customerTradeDataList != null && customerTradeDataList.size() > 0)
                {
                    isRealName = customerTradeDataList.get(0).getIsRealName();
                }
                else
                {
                    IData custInfoData = CustomerInfoQry.qryCustInfo(custId);
                    isRealName = custInfoData.getString("IS_REAL_NAME", "0");
                }
                if (!StringUtils.equals("1", isRealName))
                {
                    isSendOfficeOpenStop = true;
                }
            }
        }

        if (isSendOfficeOpenStop)
        {
            // 设置保留字段，服开获取标记做局停处理
            btd.getMainTradeData().setRsrvStr6("1");
            userTradeData.setRsrvNum1("1"); // 更新用户表标记,用于实名制
        }
    }
}
