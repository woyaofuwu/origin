package com.asiainfo.veris.crm.order.soa.person.busi.plat.feeprotect.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.feeprotect.order.requestdata.FeeProtectReqData;

public class BuildFeeProtectData extends BaseBuilder implements IBuilder {

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brq)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new FeeProtectReqData();
	}
}
