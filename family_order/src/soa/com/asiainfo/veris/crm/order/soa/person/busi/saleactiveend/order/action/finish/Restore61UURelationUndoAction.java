
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

public class Restore61UURelationUndoAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        //返销237业务时，恢复61关系
    	String tradeTypeCode =  mainTrade.getString("TRADE_TYPE_CODE", "");
    	String cancelTag =  mainTrade.getString("CANCEL_TAG", "");
    	String serialNumber =  mainTrade.getString("SERIAL_NUMBER", "");

        if (!"0".equals(cancelTag) && "237".equals(tradeTypeCode))
        {
        	boolean tag = false;
        	IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID",""));
        	if (IDataUtil.isNotEmpty(tradeSaleActive))
        	{
        		for (int index = 0, size = tradeSaleActive.size(); index < size; index++)
                {
                	String salePackageId = tradeSaleActive.getData(index).getString("PACKAGE_ID", "");
                	if(("84074847".equals(salePackageId) || "84074849".equals(salePackageId)))
                	{
                		tag = true;
                	}

                }
        	}    	
        	
        	IDataset oldTradeInfos = TradeHistoryInfoQry.queryTradeHisInfo(serialNumber, "385", "0");

        	if (tag && IDataUtil.isNotEmpty(oldTradeInfos))
        	{
        		String oldtradeId = oldTradeInfos.getData(0).getString("TRADE_ID", "");
                
                IData pdData = new DataMap();
                pdData.put("REMARKS", "多人约消关系解散返销");
                pdData.put("TRADE_ID", oldtradeId);
                pdData.put("IS_CHECKRULE", false);
                pdData.put(Route.ROUTE_EPARCHY_CODE,mainTrade.getString("TRADE_EPARCHY_CODE","0898"));

                CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", pdData);
        	}
        }

    }

}
