package com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet.order.requestdata.OpenElectrombileUserReqData;

public class OpenElectrombileUserBuilder extends BaseBuilder implements IBuilder{

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {

		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)brd;
		String operType = param.getString("OPERATION_TYPE_CODE");
		reqData.setOperType(operType);
		if("1".equals(operType))
		{
			reqData.setIsPayFee(param.getString("IS_PAY_FEE"));
			if(StringUtils.isNotBlank(param.getString("OLD_SERIAL_NUMBER")))
			{
				reqData.setMainUca(UcaDataFactory.getNormalUca(param.getString("OLD_SERIAL_NUMBER")));
			}
			
			reqData.setServiceId(param.getString("SERVICE_ID"));
			reqData.setDiscntCode(param.getString("DISCNT_CODE"));
		}
		else if("4".equals(operType))
		{
			reqData.setDiscntCode(param.getString("DISCNT_CODE"));
		}
		else if("5".equals(operType))
		{
			reqData.setIsPayFee(param.getString("IS_PAY_FEE"));
			if(StringUtils.isNotBlank(param.getString("OLD_SERIAL_NUMBER")))
			{
				reqData.setMainUca(UcaDataFactory.getNormalUca(param.getString("OLD_SERIAL_NUMBER")));
			}
		}
		
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new OpenElectrombileUserReqData();
	}

}
