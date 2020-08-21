
package com.asiainfo.veris.crm.order.soa.person.busi.imschangeproduct.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.imschangeproduct.order.requestdata.IMSChangeProductRequestData;

public class BuildIMSChangeProductReqData extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest.BuildChangeProduct implements IBuilder 
{
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
    	super.buildBusiRequestData(data, brd);
    	IMSChangeProductRequestData request = (IMSChangeProductRequestData) brd;		
		request.setMobileSerialNumber(data.getString("MOBILE_SERIAL_NUMBER", ""));
		request.setMobileProductId(data.getString("MOBILE_PRODUCT_ID", ""));
		request.setIMSProductName(data.getString("IMS_PRODUCT_NAME", ""));

    }
	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new IMSChangeProductRequestData();
	}
}
