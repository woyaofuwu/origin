package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

/**
 * @Description: REQ202005070031	动感地带联名号卡产品的配置需求
 * @author: wangsc10
 * @date: 2020-5-13
 */
public class BenefitMZoneFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String custName = mainTrade.getString("CUST_NAME");
		String userId = mainTrade.getString("USER_ID");
    	IDataset productTrades = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
    	if (IDataUtil.isNotEmpty(productTrades))
        {
			for(int i=0;i<productTrades.size();i++){
				IData productTrade = productTrades.getData(i);
				String productId = productTrade.getString("PRODUCT_ID");
				String proStartDate = productTrade.getString("START_DATE");
				if((productId.equals("84019040") || productId.equals("84019041")) && SysDateMgr.compareTo(proStartDate,SysDateMgr.getSysTime()) < 0){//不是预约的主产品
					IData mzoneInfo = new DataMap();
					mzoneInfo.put("SERIAL_NUMBER", serialNumber);
					mzoneInfo.put("PRODUCT_ID", productId);
					mzoneInfo.put("CUST_NAME", custName);
					mzoneInfo.put("USER_ID", userId);
					IDataset resultZones = CSAppCall.call("SS.BenefitMZoneIntfSVC.benefitMZone", mzoneInfo);
					if(IDataUtil.isNotEmpty(resultZones)){
						IData resultZone = resultZones.first();
						String resultCode = resultZone.getString("X_RESULTCODE");
						String resultInfo = resultZone.getString("X_RESULTINFO");
						if(!resultCode.equals("0000")){
							CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo);
						}
					}
				}
			}
        }
	}
}