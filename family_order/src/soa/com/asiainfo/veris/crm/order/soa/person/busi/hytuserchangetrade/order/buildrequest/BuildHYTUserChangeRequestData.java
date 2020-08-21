package com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade.order.buildrequest;



import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade.order.requestdata.CreateHYTUserChangeRequestData;

public class BuildHYTUserChangeRequestData extends BaseBuilder implements IBuilder {

	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {

		CreateHYTUserChangeRequestData createHYTUserChangeRequestData = (CreateHYTUserChangeRequestData) brd;

		createHYTUserChangeRequestData.setIsShipOwner(param.getString("IS_SHIP_OWNER"));
		createHYTUserChangeRequestData.setDiscntCode(param.getString("DISCNT_CODE"));
		createHYTUserChangeRequestData.setDiscntName(param.getString("DISCNT_NAME"));
		createHYTUserChangeRequestData.setShipId(param.getString("SHIP_ID"));
		createHYTUserChangeRequestData.setIsShipOwnerDiscnt(param.getString("IS_OWNER_DISCNT"));
		createHYTUserChangeRequestData.setMonthOffset("0".equals(param.getString("IS_OWNER_DISCNT"))?1:36);
		createHYTUserChangeRequestData.setIsInterface(StringUtils.isNotBlank(param.getString("INTERFACE_FLAG"))?"1":"0");

	}

	

	public BaseReqData getBlankRequestDataInstance() {
		return new CreateHYTUserChangeRequestData();
	}

}
