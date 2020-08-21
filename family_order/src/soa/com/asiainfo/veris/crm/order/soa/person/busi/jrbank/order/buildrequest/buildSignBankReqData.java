package com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.requestdata.CreateSignBankReqData;

public class buildSignBankReqData extends BaseBuilder implements IBuilder {

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		// TODO Auto-generated method stub
		CreateSignBankReqData reqData = (CreateSignBankReqData)brd;
		String payType = param.getString("PAY_TYPE","0");
		String recvBank = param.getString("BANK_ID");
		String userAcct = param.getString("BANK_ACCT_ID");
		String rech_threshold = param.getString("RECH_THRESHOLD","0");
		String rech_amount = param.getString("RECH_AMOUNT","0");
		
		String isIntf = param.getString("IS_INTF","0");
		
		reqData.setPayType(payType);
		reqData.setRecvBank(recvBank);
		reqData.setUserAcct(userAcct);
		reqData.setRechThreshold(rech_threshold);
		reqData.setRechAmount(rech_amount);
		reqData.setIsIntf(isIntf);
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new CreateSignBankReqData();
	}

}
