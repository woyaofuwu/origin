package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * 处理信用购机批量预开户-OPEN_MODE为已返单
 * @author Administrator
 *
 */
public class DealBatchOpenCreditPayAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		IData pageData=btd.getRD().getPageRequestData();
		//如果是信用购机标识-设置为已返单
		if(IDataUtil.isNotEmpty(pageData)&&"1".equals(pageData.getString("CREDIT_PAY_TAG"))){
			btd.getRD().getUca().getUser().setOpenMode("2");
		}
		
	}

}
