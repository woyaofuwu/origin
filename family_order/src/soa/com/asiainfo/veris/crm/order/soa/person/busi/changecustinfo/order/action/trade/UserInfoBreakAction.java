package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;


public class UserInfoBreakAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		 //页面请求参数
		 IData pageRequestData = btd.getRD().getPageRequestData();
	     String psptID= pageRequestData.getString("PSPT_ID");
	     
	     IData params = new DataMap();
	     params.put("PSTP_ID", psptID);
	     IDataset result = CSAppCall.call("SS.UserInfoBreakQrySVC.breQryUserInfo", params);
	     
	     if(IDataUtil.isNotEmpty(result)){
	    	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"所输入的证件号为黑名单，不能提交业务！");
	     }
	}

}
