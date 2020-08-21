package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.action.undofinish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class CancelSmartDealFeeAction implements ITradeFinishAction {

	protected static Logger log = Logger.getLogger(CancelSmartDealFeeAction.class);

	/*
	 * RSRV_NUM1 = 费用 RSRV_STR1 = 开通时的TRADE_ID，即TF_B_TRADE表的TRADE_ID RSRV_STR2 =
	 * 存临时账户的流水号OUTER_TRADE_ID RSRV_STR3 = 退回的现金存折编码
	 */

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		System.out.println("CancelSmartDealFeeActionxxxxxxxxxxxxxx26 " + mainTrade);
		String tradeId = mainTrade.getString("TRADE_ID");
		String SerialNumber = mainTrade.getString("SERIAL_NUMBER");
//		String userid = mainTrade.getString("USER_ID");
		IDataset othersTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);

		if (IDataUtil.isNotEmpty(othersTradeInfos)) {
			for (int i = 0; i < othersTradeInfos.size(); i++) {
				String rsrv_num1 = othersTradeInfos.getData(i).getString("RSRV_NUM1", "0");// RSRV_NUM1 = 费用
				String rsrv_str2 = othersTradeInfos.getData(i).getString("RSRV_STR2", "0");// RSRV_STR2 = 存临时账户的流水号OUTER_TRADE_ID
				String rsrv_str3 = othersTradeInfos.getData(i).getString("RSRV_STR3", "0");// RSRV_STR3 = 退回的现金存折编码
				if("0".equals(rsrv_num1)){
					continue;
				}
				IData params = new DataMap();
				params.put("OUTER_TRADE_ID", rsrv_str2);
				params.put("SERIAL_NUMBER", SerialNumber);
//				params.put("DEPOSIT_CODE_OUT", rsrv_str3);// 账务给存折后修改
				params.put("TRADE_FEE", rsrv_num1);
				params.put("CHANNEL_ID", "15000");
				params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
				params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
				params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
				params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
				params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
				System.out.println("CancelSmartDealFeeActionxxxxxxxxxxxxxx49 " + params);
				IData resultData = AcctCall.transFeeOutADSL(params);
				String result = resultData.getString("RESULT_CODE", "");

				if ("".equals(result) || !"0".equals(result)) {
					CSAppException.appError("61312", "调用接口AM_CRM_TransFeeOutADSL转存押金入参：" + params + "错误:" + resultData.getString("RESULT_INFO"));
				}
				
			}
		}
	}

}
