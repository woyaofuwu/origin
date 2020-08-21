package com.asiainfo.veris.crm.order.soa.person.busi.createhytusertrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.createhytusertrade.order.requestdata.CreateHYTPersonUserRequestData;

public class BuildCreateHYTPersonUserRequestData extends BaseBuilder implements IBuilder {

	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {

		CreateHYTPersonUserRequestData createHYTPersonUserRequestData = (CreateHYTPersonUserRequestData) brd;

		createHYTPersonUserRequestData.setIsShipOwner(param.getString("IS_SHIP_OWNER"));
		createHYTPersonUserRequestData.setDiscntCode(param.getString("DISCNT_CODE"));
		createHYTPersonUserRequestData.setDiscntName(param.getString("DISCNT_NAME"));
		createHYTPersonUserRequestData.setShipId(param.getString("SHIP_ID"));
		createHYTPersonUserRequestData.setIsShipOwnerDiscnt(param.getString("IS_OWNER_DISCNT"));
		createHYTPersonUserRequestData.setMonthOffset("0".equals(param.getString("IS_OWNER_DISCNT"))?1:36);

	}

	

	public BaseReqData getBlankRequestDataInstance() {
		return new CreateHYTPersonUserRequestData();
	}

}
