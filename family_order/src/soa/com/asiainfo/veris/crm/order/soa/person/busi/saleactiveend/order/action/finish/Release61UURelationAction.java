
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

public class Release61UURelationAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        //终止多人约消活动时调用接口解散61关系
    	String tradeTypeCode =  mainTrade.getString("TRADE_TYPE_CODE", "");
    	IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID",""));
        if (IDataUtil.isEmpty(tradeSaleActive) || !"237".equals(tradeTypeCode))
            return;
        IDataset relation61 = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61",mainTrade.getString("USER_ID",""),"1");
        for (int index = 0, size = tradeSaleActive.size(); index < size; index++)
        {
        	String salePackageId = tradeSaleActive.getData(index).getString("PACKAGE_ID", "");
        	if( IDataUtil.isNotEmpty(relation61) && ("84074847".equals(salePackageId) || "84074849".equals(salePackageId)))
        	{
        		mainTrade.put("END_DATE",tradeSaleActive.getData(index).getString("END_DATE", ""));
        		CSAppCall.call("SS.DestroyGroupRegSVC.tradeReg", mainTrade);
        	}
        }

    }

}
