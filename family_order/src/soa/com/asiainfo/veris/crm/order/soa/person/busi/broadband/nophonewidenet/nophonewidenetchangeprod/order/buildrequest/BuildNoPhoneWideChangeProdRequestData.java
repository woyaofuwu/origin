package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest.BuildChangeProduct;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.requestdata.WidenetProductRequestData;

public class BuildNoPhoneWideChangeProdRequestData extends BuildChangeProduct implements IBuilder {

	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		super.buildBusiRequestData(param, brd);
		
		WidenetProductRequestData request = (WidenetProductRequestData) brd;
		
		request.setWideActivePayFee(param.getString("WIDE_ACTIVE_PAY_FEE","0"));
		request.setYearDiscntRemainFee(param.getString("YEAR_DISCNT_REMAIN_FEE","0"));
		request.setRemainFee(param.getString("REMAIN_FEE","0"));
		request.setAcctReainFee(param.getString("ACCT_REMAIN_FEE","0"));

	}

	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new WidenetProductRequestData();
	}

}
