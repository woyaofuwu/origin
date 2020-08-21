
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

/**
 * 1、对于当月复机，营销活动恢复为销户前的一致，同时营销活动赠送的信用度也相应恢复。 
 * 2、对于次月复机，营销活动不再恢复，同时调账务接口终止营销活动赠送的信用度。
 * <p>Title: RestoreSaleactiveEndAction</p>
 * <p>Description: </p>
 * <p>Company: AsiaInfo</p>
 * @author Administrator
 * @date 2017-7-17 下午12:00:36
 */
public class RestoreSaleactiveEndAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        IDataset cancelSaleActiveList = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));
        if (IDataUtil.isNotEmpty(cancelSaleActiveList))
		{
        	for (int i = 0, size = cancelSaleActiveList.size(); i < size; i++)
			{
        		IData cancelSale = cancelSaleActiveList.getData(i);

        		if("复机隔月恢复用户活动数据！".equals(cancelSale.getString("REMARK"))){
        			IData cancelParam = new DataMap();
    				cancelParam.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
    				cancelParam.put("PRODUCT_ID", cancelSale.getString("PRODUCT_ID"));
    				cancelParam.put("PACKAGE_ID", cancelSale.getString("PACKAGE_ID"));
    				cancelParam.put("RELATION_TRADE_ID", cancelSale.getString("RELATION_TRADE_ID"));
    				cancelParam.put("FORCE_END_DATE", SysDateMgr.getSysDate());
    				cancelParam.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
    				cancelParam.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
    				cancelParam.put("TRADE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
    				cancelParam.put("TRADE_EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
    				cancelParam.put("REMARK", "对于次月复机，营销活动不再恢复，同时调账务接口终止营销活动赠送的信用度");

    				CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", cancelParam).getData(0);

        		}
				
			}
		}
    }
}
