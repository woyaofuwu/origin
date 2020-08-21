
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreUserDealOfficeStopAction.java
 * @Description: 复机后对用户做局方停机处理
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-16 上午10:14:15
 */
public class RestoreUserGHDealOfficeStopAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        boolean isDealOfficeStop = false;
        String strTradeId = mainTrade.getString("TRADE_ID");
        // 取用资料的时候，最好获取台账表数据，取用户最新的数据，如果没有台账数据，再取资料表数据
        IDataset tradeUserDataset = TradeUserInfoQry.getTradeUserByTradeId(strTradeId);
        // 个人用户复机或无线固话复机完工时，对于待激活号码未登记实名用户的复机，则给用户发送局方停机指令
        String acctTag = "";
        if (IDataUtil.isNotEmpty(tradeUserDataset))
        {
            acctTag = tradeUserDataset.getData(0).getString("ACCT_TAG", "0");
            String custId = tradeUserDataset.getData(0).getString("CUST_ID", "0");
            if ("2".equals(acctTag))
            { // 待激活用户
                String isRealName = "";
                IDataset tradeCustDataset = TradeCustomerInfoQry.getTradeCustomerByTradeId(strTradeId);
                if (IDataUtil.isNotEmpty(tradeCustDataset))
                {
                    isRealName = tradeCustDataset.getData(0).getString("IS_REAL_NAME", "0");
                }
                else
                {
                    IData custInfoData = CustomerInfoQry.qryCustInfo(custId);
                    isRealName = custInfoData.getString("IS_REAL_NAME", "0");
                }
                if ("0".equals(isRealName))
                {
                    isDealOfficeStop = true;
                }
            }
        }

        if (isDealOfficeStop)
        {
            // 调用局方停机,trade_type_code=7231(局停发联指)

        }

    }

}
