package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.afterprint;  


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IPrintFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeFinish;


public class TradeFinishAPIWithOutMove implements IPrintFinishAction
{
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        
        if (StringUtils.isNotBlank(tradeId))
        {
            IDataset mainTradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
            if (IDataUtil.isNotEmpty(mainTradeInfos))
            {
                IData mainTradeInfo = mainTradeInfos.getData(0);
                TradeFinish.finishAPIWithOutMove(mainTradeInfo);
            }
        }
    } 
}
