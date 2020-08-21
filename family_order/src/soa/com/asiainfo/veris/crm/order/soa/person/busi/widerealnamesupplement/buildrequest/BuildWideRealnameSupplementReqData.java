package com.asiainfo.veris.crm.order.soa.person.busi.widerealnamesupplement.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.widerealnamesupplement.requestdata.WideRealnameSupplementReqData;

public class BuildWideRealnameSupplementReqData extends BaseBuilder implements IBuilder{

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)throws Exception {
		
		WideRealnameSupplementReqData reqData = (WideRealnameSupplementReqData) brd;
		reqData.setserialNumber(param.getString("SERIAL_NUMBER"));
		reqData.setCustName(param.getString("CUST_NAME"));
		reqData.setPsptId(param.getString("PSPT_ID"));
		reqData.setPsptAddr(param.getString("CUST_PSPT_ADDR"));
		reqData.setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
		reqData.setAgentCustName(param.getString("AGENT_CUST_NAME"));
		reqData.setAgentPsptId(param.getString("AGENT_PSPT_ID"));
		reqData.setAgentPsptTypeCode(param.getString("AGENT_PSPT_TYPE_CODE"));
		reqData.setAgentPsptAddr(param.getString("AGENT_PSPT_ADDR"));
		reqData.setRSRV_STR2(param.getString("RSRV_STR2"));
		reqData.setRSRV_STR3(param.getString("RSRV_STR3"));
		reqData.setRSRV_STR4(param.getString("RSRV_STR4"));
		reqData.setRSRV_STR5(param.getString("RSRV_STR5"));
		
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		return new WideRealnameSupplementReqData();
	}

}
