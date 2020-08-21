
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * @CREATED 备卡激活返销接口
 */
public class UndoModiyVipSimBakActAction implements ITradeFinishAction
{
    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
        String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
        IDataset resTradeInfos = TradeResInfoQry.qryTradeResByTradeTypeMtag(tradeId, "1", "0");
        if (IDataUtil.isNotEmpty(resTradeInfos))
        {
            String resCode = resTradeInfos.getData(0).getString("RES_CODE");
            CustVipInfoQry.updateVipSimBak(resCode, tradeStaffId, tradeDepartId);
        }
    }

}
