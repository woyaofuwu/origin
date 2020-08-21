package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.dsmp;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public class DsmpIbossOutFilter implements IFilterOut {

	@Override
	public IData transfterDataOut(IData input, BusiTradeData btd)
			throws Exception {
		input.put("OPR_NUMB",
				input.getString("TRANS_ID", input.getString("OPR_NUMB")));
		input.put("TRANS_ID",
				input.getString("TRANS_ID", input.getString("OPR_NUMB")));
		input.put("INTF_TRADE_ID",
				input.getString("TRANS_ID", input.getString("INTF_TRADE_ID")));
		input.put("ID_TYPE", input.getString("ID_TYPE", "01"));
		input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));

		if (BofConst.PRE_TYPE_SMS_CONFIRM.equals(btd.getRD().getPreType())
				|| PlatConstants.OPER_CANCEL_ALL.equals(input.get("OPER_CODE"))
				|| PlatConstants.OPER_SP_CANCEL_ALL.equals(input
						.get("OPER_CODE"))) {
			input.put("X_CHECK_TAG", "1");
		} else {
			input.put("X_CHECK_TAG", "0");
		}
		input.put("RSLT", "00");

		return input;
	}
}
