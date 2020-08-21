package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;

/**
 * @ClassName: DealForAgentPresentFeeAction.java
 * @Description: 保存身份证号码和地址信息
 * @version: v1.0.0
 * @author: liquan
 */
public class SavePsptIdAndPsptAdrrAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {

		System.out.println("ModifyCustOnePsptIdMoreNameActionxxxxxxxxxxxxxx " + mainTrade);

		String tradeId = mainTrade.getString("TRADE_ID");
		IDataset custpersonDs = TradeCustPersonInfoQry.getTradeCustPersonByTradeId(tradeId);
		if (DataSetUtils.isNotBlank(custpersonDs)) {
			IData custPersonData = custpersonDs.getData(0);
			String psptType = custPersonData.getString("PSPT_TYPE_CODE", "").trim();
			if (psptType.equals("0") || psptType.equals("1") || psptType.equals("2") || psptType.equals("3")) {// 本地、外地身份证、户口
				String psptId = custPersonData.getString("PSPT_ID", "").trim();
				String psptAddr = custPersonData.getString("PSPT_ADDR", "").trim();
				if (psptAddr.trim().length() > 0 && psptId.trim().length() > 0) {
					IData input = new DataMap();
					input.put("PSPT_ID", psptId);
					input.put("PSPT_ADDR", psptAddr);
					CSAppCall.call("SS.ModifyCustInfoSVC.SavePsptIdAndPsptAdrr", input);
				}
			}
		}
	}

}
