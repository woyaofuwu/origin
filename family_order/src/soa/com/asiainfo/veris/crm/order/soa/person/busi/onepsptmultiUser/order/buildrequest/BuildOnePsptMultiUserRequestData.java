package com.asiainfo.veris.crm.order.soa.person.busi.onepsptmultiUser.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.onepsptmultiUser.order.requestdata.OnePsptMultiUserRequestData;

public class BuildOnePsptMultiUserRequestData extends BaseBuilder implements IBuilder{

	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
		OnePsptMultiUserRequestData data = (OnePsptMultiUserRequestData)brd;
		data.setSerialNumber(param.getString("SERIAL_NUMBER"));
		data.setUserList(param.getDataset("userList"));
		data.setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));                                                                                                                                                                                                                                                                                                 
		data.setStaffId(param.getString("STAFF_ID"));
		data.setPsptId(param.getString("PSPT_ID"));
		data.setIsUnitPsptType(param.getString("IS_UNIT_PSPT_TYPE"));
    }
	
	@Override
	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		 return new OnePsptMultiUserRequestData();
	}
}
