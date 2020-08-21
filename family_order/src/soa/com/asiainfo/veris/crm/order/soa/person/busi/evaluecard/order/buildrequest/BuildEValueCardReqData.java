package com.asiainfo.veris.crm.order.soa.person.busi.evaluecard.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;

public class BuildEValueCardReqData extends BaseBuilder implements IBuilder {

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		String card_fee = param.getString("PAY_MONEY", "0");// 卡费
		 //购卡费
		if (Integer.parseInt(card_fee) > 0)
       {
           FeeData feeData = new FeeData();
           feeData.setFeeMode("0");
           feeData.setFeeTypeCode("20");
           feeData.setFee(card_fee);
           feeData.setOldFee(card_fee);
           brd.addFeeData(feeData);
       }

	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {

		return new BaseReqData();
	}

}
