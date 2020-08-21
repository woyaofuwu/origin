
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;


public class JXnumberConsumeAmountAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String consumeAmount = btd.getRD().getPageRequestData().getString("DATA8");
		if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "JXNUMEA")){
			if (StringUtils.isNotBlank(consumeAmount)) {
				//费用明细
				FeeTradeData feeTradeData = new FeeTradeData();
				feeTradeData.setFee(consumeAmount);
				feeTradeData.setFeeMode("2");
				feeTradeData.setFeeTypeCode("0");
				feeTradeData.setOldfee(consumeAmount);
				feeTradeData.setRemark("吉祥号码消费金额写入");
				feeTradeData.setUserId(btd.getRD().getUca().getUserId());
				btd.add(btd.getRD().getUca().getSerialNumber(), feeTradeData);
				
				//支付方式
				IData input=btd.getRD().getPageRequestData();
				input.put("X_PAY_MONEY_CODE", "R");
				
				//构造主台账费用
				MainTradeData mainTrade=btd.getMainTradeData();
				if(mainTrade!=null){
					mainTrade.setFeeState("1");
					mainTrade.setAdvancePay((consumeAmount)+"");
				}
			}
		} 
	}
}
