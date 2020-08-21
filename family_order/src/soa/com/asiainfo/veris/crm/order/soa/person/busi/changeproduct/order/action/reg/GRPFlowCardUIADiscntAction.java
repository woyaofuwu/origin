package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.GRPValueCardMgrSVC;

public class GRPFlowCardUIADiscntAction implements ITradeAction {

	private static transient Logger logger = Logger.getLogger(GRPFlowCardUIADiscntAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		List<MainTradeData> mainTrades = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
		if (discntTrades != null && discntTrades.size() > 0) {
			for (DiscntTradeData discntTrade : discntTrades) {
				String elementId = discntTrade.getElementId();
				if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag()) && "52000010".equals(elementId)) {
					IData param = new DataMap();
					param.put("CARD_NUMBER", discntTrade.getRemark());
					IDataset userCard = GRPValueCardMgrSVC.qryGrpUserIDAByCard(param);
					if (IDataUtil.isNotEmpty(userCard)) {

						IData input = userCard.getData(0);
						input.put("USER_ID", input.getString("CUST_ID"));
						input.put("CHANNEL_ID", "0");
						input.put("PAYMENT_ID", "9127");
						input.put("PAY_FEE_MODE_CODE", "302");
						input.put("DEPOSIT_CODE", "9127");
						input.put("SAIL_VALUE", input.getString("SAIL_VALUE"));
						input.put("FACE_VALUE", input.getString("FACE_VALUE"));
						input.put("OUTER_TRADE_ID", btd.getTradeId());
						input.put("TRADE_EPARCHY_CODE", "0898");// 受理地州
						input.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());// 路由地州
						input.put("TRADE_CITY_CODE", "HNSJ");
						input.put("TRADE_DEPART_ID", "36601");
						input.put("TRADE_STAFF_ID", "SUPERUSR");
						input.put("REMARK", "流量卡充值缴费");
						input.put("PAYMENT_ID_P", "9128");
						input.put("PAY_FEE_MODE_CODE_P", "23");

						try {
							IDataset result = AcctCall.FlowCardTransFee(input);
						} catch (Exception e) {
							CSAppException.apperr(ProductException.CRM_PRODUCT_522, "调账务缴费接口失败");
						}

						discntTrade.setUserIdA(input.getString("CUST_ID", "-1"));
						MainTradeData data = mainTrades.get(0);
						data.setRsrvStr3("52000010");
						btd.setNeedSms(false);
					}
				}
			}
		}
	}
}
