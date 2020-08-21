
package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: OccupyPhoneAction.java
 * @Description: 商务电话固话号码实占
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-26 下午3:34:51 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-26 yxd v1.0.0 修改原因
 */
public class OccupyPhoneAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset custSet = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
        if (DataSetUtils.isNotBlank(custSet))
        {
            IData custData = custSet.first();
            PBossCall.resRealOccupy(mainTrade.getString("SERIAL_NUMBER"), custData.getString("PSPT_ID"), mainTrade.getString("USER_ID"));
        }
    }

}
