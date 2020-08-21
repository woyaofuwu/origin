
package com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.requsetdata.OceanFrontReqData;

public class BuildOceanFrontReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	OceanFrontReqData war= (OceanFrontReqData)brd;
    	war.setOceanRmark(param.getString("REMARK"));//备注
    	war.setOpenStopType(param.getString("OPEN_STOP_TYPE"));
    }

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new OceanFrontReqData();
	}



}
