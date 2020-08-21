package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;

import org.apache.log4j.Logger;

public class TradePocFinishRegistVpnAction implements ITradeFinishAction {

	private static final Logger logger = Logger.getLogger(TradePocFinishRegistVpnAction.class);

	public void executeAction(IData mainTrade) throws Exception {
		String userid = mainTrade.getString("USER_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String productId = mainTrade.getString("PRODUCT_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String ecUserId = mainTrade.getString("USER_ID_B");
		String tradeId = mainTrade.getString("TRADE_ID");
		if ("4694".equals(tradeTypeCode) || "4697".equals(tradeTypeCode)) 
		{
			IDataset mebDataset = ProductMebInfoQry.getProductMebByPidB(productId);
			if (IDataUtil.isNotEmpty(mebDataset)) 
			{
				String productMeb = mebDataset.getData(0).getString("PRODUCT_ID");
				IDataset merchDataset = ProductCompRelaInfoQry.queryProductRealByProductB(productMeb);
				if (IDataUtil.isNotEmpty(merchDataset)) 
				{
					if ("9604011".equals(productId)) 
					{
						IDataset serviceDataSet = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
						for (int i = 0; i < serviceDataSet.size(); i++) 
						{
							IData serviceData = serviceDataSet.getData(i);

							if ("991101".equals(serviceData.getString("SERVICE_ID"))) {
								serviceData.put("TRADE_STAFF_ID", "IBOSS000");
								serviceData.put("TRADE_DEPART_ID", "00309");
								serviceData.put("TRADE_CITY_CODE", "INTF");
								serviceData.put("TRADE_EPARCHY_CODE", "INTF");
								serviceData.put("IN_MODE_CODE", "6");
								serviceData.put("ROUTE_EPARCH_CODE", "0898");
								serviceData.put("SERIAL_NUMBER", serialNumber);
								serviceData.put("USER_ID",userid);
								serviceData.put("EC_USER_ID", ecUserId);
								if ("4694".equals(tradeTypeCode)) 
								{
									serviceData.put("MODIFY_TAG", 2);
								}
								serviceData.put("START_DATE", SysDateMgr.getSysTime());
								CSAppCall.call("SS.PocMebRegistVpnSVC.crtTrade", serviceData);
							}
						}

					}
				}

			}

		}
	}

}
