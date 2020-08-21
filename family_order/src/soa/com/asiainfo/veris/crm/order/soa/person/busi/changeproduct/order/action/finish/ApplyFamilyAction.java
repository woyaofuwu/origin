package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

public class ApplyFamilyAction implements ITradeFinishAction
{
	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String execTime = mainTrade.getString("ACCEPT_DATE", SysDateMgr.getSysDate());
		String rsrvStr3 = mainTrade.getString("RSRV_STR3", execTime);
		String rsrvStr7 = mainTrade.getString("RSRV_STR7", "");
		String rsrvStr8 = mainTrade.getString("RSRV_STR8", "");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		IDataset productTrades = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
    	if (IDataUtil.isNotEmpty(productTrades))
        {
    		for (int i=0;i<productTrades.size();i++)
            {
    			IData product = productTrades.getData(i);
    			String productId = product.getString("PRODUCT_ID", "");
                String modifyTag = product.getString("MODIFY_TAG", "");
                
                if ("80003014".equals(productId) && "0".equals(modifyTag) )
    			{
    				// 办理统付业务登记
    				IDataset memberdatas = new DatasetList();
    				IData membone = new DataMap();
    				IData membtwo = new DataMap();
    				membone.put("SERIAL_NUMBER_B", rsrvStr7);
    				membone.put("START_DATE", rsrvStr3);
    				membone.put("END_DATE", "2050-12-31 23:59:59");
    				membone.put("MODIFY_TAG", "0");
    				memberdatas.add(membone);
    	
    				membtwo.put("SERIAL_NUMBER_B", rsrvStr8);
    				membtwo.put("START_DATE", rsrvStr3);
    				membtwo.put("END_DATE", "2050-12-31 23:59:59");
    				membtwo.put("MODIFY_TAG", "0");
    				memberdatas.add(membtwo);
    	
    				IData tradeData = new DataMap();
    				tradeData.put("SERIAL_NUMBER", serialNumber);
    				tradeData.put("AUTH_SERIAL_NUMBER", serialNumber);
    				tradeData.put("MEMBER_DATAS", memberdatas);
    				tradeData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
    				tradeData.put("REMARK", "158不限量套餐连带办理"+tradeId);
    				IDataset dataset = CSAppCall.call("SS.FamilyUnionPayRegSVC.tradeReg", tradeData);
    			}
            }
        }
	}
}
