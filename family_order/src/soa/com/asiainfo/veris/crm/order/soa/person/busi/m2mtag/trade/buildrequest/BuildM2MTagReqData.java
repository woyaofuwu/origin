package com.asiainfo.veris.crm.order.soa.person.busi.m2mtag.trade.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.m2mtag.trade.requestdata.M2MTagReqData;

public class BuildM2MTagReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

    }

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new M2MTagReqData();
	}



}
