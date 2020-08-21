package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.requestdata.MfcSubDiscntReqData;

public class BuildMfcSubDiscntReqData extends BaseBuilder implements IBuilder 
{

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
	{
		MfcSubDiscntReqData reqData = (MfcSubDiscntReqData) brd;
		reqData.setUserId(param.getString("USER_ID_B"));
		reqData.setUserIdA(param.getString("USER_ID_A"));
		reqData.setUserId(param.getString("USER_ID_B"));
		reqData.setModifyTag(param.getString("MODIFY_TAG"));
		reqData.setRoleCode(param.getString("ROLE_CODE_B"));
		reqData.setProductCode(param.getString("PRODUCT_CODE"));
		reqData.setProductOfferingID(param.getString("PRODUCT_OFFERING_ID"));
		reqData.setStartDate(param.getString("START_DATE"));
		reqData.setEndDate(param.getString("END_DATE"));
	}

	@Override
	public BaseReqData getBlankRequestDataInstance()
	{
		// TODO Auto-generated method stub
		return new MfcSubDiscntReqData();
	}

}
