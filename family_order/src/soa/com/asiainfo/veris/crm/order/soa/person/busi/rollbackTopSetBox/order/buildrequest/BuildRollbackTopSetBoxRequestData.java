package com.asiainfo.veris.crm.order.soa.person.busi.rollbackTopSetBox.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.rollbackTopSetBox.order.requestdata.RollbackTopSetBoxRequestData;

public class BuildRollbackTopSetBoxRequestData extends BaseBuilder implements IBuilder{

	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
		RollbackTopSetBoxRequestData data=(RollbackTopSetBoxRequestData)brd;
		data.setSerialNumber(param.getString("SERIAL_NUMBER"));
		
    }
	
	public BaseReqData getBlankRequestDataInstance()
    {
        return new RollbackTopSetBoxRequestData();
    }
}
