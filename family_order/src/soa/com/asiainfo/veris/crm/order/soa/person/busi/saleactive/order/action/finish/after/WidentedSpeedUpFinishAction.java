package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

import org.apache.log4j.Logger;
public class WidentedSpeedUpFinishAction implements ITradeFinishAction {
	public static final Logger logger=Logger.getLogger(WidentedSpeedUpFinishAction.class);
	public void executeAction(IData mainTrade) throws Exception {
		IDataset tradeSaleActives = TradeSaleActive
				.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String tradeId = mainTrade.getString("TRADE_ID");
	    IDataset userinfo = UserInfoQry.getUserinfo("KD_"+serialNumber);
	    if (IDataUtil.isNotEmpty(userinfo)) {
            String userId = userinfo.getData(0).getString("USER_ID");
		  if (IDataUtil.isNotEmpty(tradeSaleActives)
				&& tradeSaleActives.size() > 0) {
			for (int i = 0; i < tradeSaleActives.size(); i++) {
				IData tradeSaleActive = tradeSaleActives.getData(i);
				if (tradeSaleActive.getString("MODIFY_TAG").equals(BofConst.MODIFY_TAG_ADD)) {
					String productId = tradeSaleActive.getString("PRODUCT_ID");
					String packageId = tradeSaleActive.getString("PACKAGE_ID");
					IDataset commParaInfo7979 = CommparaInfoQry.queryCommInfos("7979", "KDTS_RATE", productId, packageId);
					if (IDataUtil.isNotEmpty(commParaInfo7979) && commParaInfo7979.size() > 0) {
			
						String speed = commParaInfo7979.getData(0).getString("PARA_CODE3");
             
                    	   //调服开宽带提速
                    	   this.changeResMethod(tradeId, userId, "KD_"+serialNumber, speed, WideNetUtil.getWidenetUserRate(serialNumber));
              
					}
				}
			}

		}
	   }

	}
	
	public void changeResMethod(String TRADE_ID ,String USER_ID,String SERIAL_NUMBER ,String PRES_RATE,String OLD_PRES_RATE){		
		IData inParam = new DataMap();
		inParam.put("TRADE_ID", TRADE_ID);
		inParam.put("USER_ID", USER_ID);
		inParam.put("SERIAL_NUMBER", SERIAL_NUMBER);
		inParam.put("PRES_RATE", PRES_RATE);
		inParam.put("OLD_PRES_RATE", OLD_PRES_RATE);
    	try {
			CSAppCall.callNGPf("PF_ORDER_CHANGERES", inParam);
		} catch (Exception e) {			
			logger.debug("WidentedSpeedUpFinishAction异常"+e);
		}
	}
}
