package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;

/**
 * 依据PBOSS竣工时间更新相关台帐时间
 * 
 * @author yuyj3
 */
public class ProcessPbossFinishDateAction implements ITradeFinishAction {

	public void executeAction(IData mainTrade) throws Exception {
		String tradeId = mainTrade.getString("TRADE_ID");
		String finishDate = "";
		IDataset finishInfos = TradePbossFinishInfoQry
				.getTradePbossFinish(tradeId);
		if (IDataUtil.isEmpty(finishInfos)) {
			CSAppException.apperr(WidenetException.CRM_WIDENET_14);
		}
		finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
		TradeUserInfoQry.updateOpenDate(tradeId, finishDate);

	}

}
