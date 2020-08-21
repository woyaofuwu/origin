package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * 开户撤单释放资源预占
 * 
 * @author huanghua
 */
public class ResCancelReleaseAction implements ITradeFinishAction
{
	private static transient final Logger logger = Logger.getLogger(ResCancelReleaseAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		// TODO Auto-generated method stub
		String serialNum = mainTrade.getString("SERIAL_NUMBER", "");
		String simCardNo = "";
		String tradeId = mainTrade.getString("TRADE_ID", "");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE", "");
		String mode = "TRADE_CANCEL";

		IDataset ds = TradeResInfoQry.getTradeRes(tradeId, "1", "0");

		if (IDataUtil.isNotEmpty(ds))
		{
			simCardNo = ds.getData(0).getString("RES_CODE", "");
		}

		if (("10".equals(tradeTypeCode) || "310".equals(tradeTypeCode)) && !"".equals(serialNum))
		{
			// 释放手机号码
			ResCall.releaseAllResByNo(serialNum, "0", tradeId + "订单取消", mode);
			// 释放SIM卡
			/*
			 * if (StringUtils.isBlank(simCardNo)) { simCardNo =
			 * mainTrade.getString("RSRV_STR1", ""); }
			 */

			if (StringUtils.isBlank(simCardNo))
			{
				return;
			}

			ResCall.releaseAllResByNo(simCardNo, "1", tradeId + "订单取消", mode);
		} else if ("142".equals(tradeTypeCode))
		{
			/*
			 * if (StringUtils.isBlank(simCardNo)) { simCardNo =
			 * mainTrade.getString("RSRV_STR10", ""); }
			 */

			if (StringUtils.isBlank(simCardNo))
			{
				return;
			}

			ResCall.releaseAllResByNo(simCardNo, "1", tradeId + "订单取消", mode);
		}
	}
}
