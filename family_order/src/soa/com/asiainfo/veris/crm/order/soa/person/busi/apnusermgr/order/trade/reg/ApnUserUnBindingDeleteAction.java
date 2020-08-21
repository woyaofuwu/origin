package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.trade.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.ApnUserBindingForOlcomQry;


public class ApnUserUnBindingDeleteAction implements ITradeAction {
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		MainTradeData mainTrade = btd.getMainTradeData();
		
		String userId = mainTrade.getUserId();
		String instId = mainTrade.getRsrvStr7();
		if(StringUtils.isNotBlank(userId) 
				&& StringUtils.isNotBlank(instId))
		{
			IData data = new DataMap();
			data.put("USER_ID", userId);
			data.put("INST_ID", instId);
			data.put("REMOVE_TAG", "1");
			data.put("REMARK", "专用APN绑定IP删除!!");
			data.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
	        
			ApnUserBindingForOlcomQry.updateUserApnInfoByInstId(data);
		}
		
	}
	
}
