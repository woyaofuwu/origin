package com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.requestdata.CancelSignBankReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.requestdata.CreateSignBankReqData;

public class buildCancelBankReqData extends BaseBuilder implements IBuilder {

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		// TODO Auto-generated method stub
		CancelSignBankReqData reqData = (CancelSignBankReqData)brd;
		
		String selectValues = param.getString("SELECT_VALUES");
		String isIntf = param.getString("IS_INTF","0");
		
		reqData.setSelectValues(selectValues);
		reqData.setIsIntf(isIntf);
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new CancelSignBankReqData();
	}

}
