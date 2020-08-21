package com.asiainfo.veris.crm.order.soa.person.busi.data.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.data.order.requestdata.DataDonateRequestData;

public class BuildDataDonateRequestData extends BaseBuilder implements IBuilder {

	   @Override
	    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
	    {
	        DataDonateRequestData reqData = (DataDonateRequestData) brd;
	        reqData.setDonateData(param.getString("DONATE_DATA",""));
	        reqData.setBalance(param.getString("BALANCE",""));
	        reqData.setObjSerialNumber(param.getString("OBJ_SERIAL_NUMBER",""));
	        reqData.setCommID(param.getString("COMM_ID",""));
	        reqData.setDataType(param.getString("DATA_TYPE",""));
	        reqData.setEffectiveDate(param.getString("EFFECTIVE_DATE",""));
	        reqData.setExpireDate(param.getString("EXPIRE_DATE",""));
	        reqData.setDiscntCode(param.getString("DISCNT_CODE",""));
	    }

	    @Override
	    public BaseReqData getBlankRequestDataInstance()
	    {
	        return new DataDonateRequestData();
	    }

}
