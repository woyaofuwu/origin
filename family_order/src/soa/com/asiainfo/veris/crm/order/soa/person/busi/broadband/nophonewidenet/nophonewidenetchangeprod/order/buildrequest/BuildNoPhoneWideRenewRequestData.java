package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest.BuildChangeProduct;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order.requestdata.NoPhoneWideRenewRequestData;

public class BuildNoPhoneWideRenewRequestData extends BuildChangeProduct implements IBuilder {

	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception { 
		
		NoPhoneWideRenewRequestData request = (NoPhoneWideRenewRequestData) brd; 
		request.setProductId(param.getString("PRODUCT_ID",""));
		request.setPackageId(param.getString("PACKAGE_ID",""));
		request.setDiscntId(param.getString("DISCNT_CODE",""));
		request.setStartDate(param.getString("START_DATE",""));
		request.setEndDate(param.getString("END_DATE",""));
		request.setWideYearFee(param.getString("FEE_YEAR",""));
		request.setWideFirstMonFee(param.getString("FEE_DAY",""));
		request.setStopOpenTag(param.getString("STOP_OPEN_TAG",""));
	}

	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new NoPhoneWideRenewRequestData();
	}

}
