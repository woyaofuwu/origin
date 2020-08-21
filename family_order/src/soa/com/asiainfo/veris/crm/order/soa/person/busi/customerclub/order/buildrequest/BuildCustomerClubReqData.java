
package com.asiainfo.veris.crm.order.soa.person.busi.customerclub.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.customerclub.order.requsetdata.CustomerClubReqData;

public class BuildCustomerClubReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	CustomerClubReqData war= (CustomerClubReqData)brd;
    	war.setCustName(param.getString("CUST_NAME"));
    	war.setClubType(param.getString("CLUB_TYPE"));
    }

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new CustomerClubReqData();
	}



}
