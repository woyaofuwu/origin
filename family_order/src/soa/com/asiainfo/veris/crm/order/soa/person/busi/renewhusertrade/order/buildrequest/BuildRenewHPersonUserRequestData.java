package com.asiainfo.veris.crm.order.soa.person.busi.renewhusertrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.renewhusertrade.order.requestdata.RenewHPersonUserRequestData;

public class BuildRenewHPersonUserRequestData extends BaseBuilder implements IBuilder {

	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
		
	}

	public BaseReqData getBlankRequestDataInstance() {
		return new RenewHPersonUserRequestData();
	}
}
