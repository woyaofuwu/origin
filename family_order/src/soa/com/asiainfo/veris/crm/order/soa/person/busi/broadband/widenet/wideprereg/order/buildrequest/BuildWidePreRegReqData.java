package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.order.requestdata.WidePreRegRequestData;

public class BuildWidePreRegReqData extends BaseBuilder implements IBuilder{

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		WidePreRegRequestData reqData = (WidePreRegRequestData) brd;
		reqData.setArea_code(param.getString("AREA_CODE"));
		reqData.setContact_sn(param.getString("CONTACT_SN"));
		reqData.setCust_name(param.getString("CUST_NAME"));
		reqData.setPre_cause(param.getString("PRE_CAUSE"));
		reqData.setReg_status("1");
		reqData.setRemark(param.getString("REMARK"));
		reqData.setSet_addr(param.getString("SET_ADDR"));
		reqData.setWbbw(param.getString("WBBW"));
		reqData.setAddr_code(param.getString("ADDR_CODE"));
		reqData.setHome_addr(param.getString("HOME_ADDR"));
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {

		return new WidePreRegRequestData();
	}
	

  
	
}
