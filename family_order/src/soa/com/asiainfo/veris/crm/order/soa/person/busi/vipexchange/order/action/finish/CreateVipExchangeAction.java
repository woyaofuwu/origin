
package com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

/**
 * 创建VipExchange资料
 * 
 * @author zhouwu
 * @date 2014-08-26 22:19:28
 */
public class CreateVipExchangeAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");

        IDataset tradeOthers = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);

        for (int i = 0, size = tradeOthers.size(); i < size; i++)
        {
            String instId = tradeOthers.getData(i).getString("INST_ID");
            String acceptMonth = tradeOthers.getData(i).getString("ACCEPT_MONTH");

            IData param = new DataMap();
            param.put("TRADE_ID", tradeId);
            param.put("INST_ID", instId);
            param.put("ACCEPT_MONTH", acceptMonth);

//            Dao.executeUpdateByCodeCode("TF_F_CUST_VIP", "INS_VIP_EXCHANGE_GIFT", param);
            IDataset otherTradeInfos = Dao.qryByCode("TF_F_CUST_VIP", "SET_VIP_EXCHANGE_GIFT", param, Route.getJourDb());
            if(IDataUtil.isNotEmpty(otherTradeInfos)){
            	IData otherTradeInfo = otherTradeInfos.getData(0);
            	Dao.insert("TF_F_VIP_EXCHANGE", otherTradeInfo);
            }
        }
    }
}
