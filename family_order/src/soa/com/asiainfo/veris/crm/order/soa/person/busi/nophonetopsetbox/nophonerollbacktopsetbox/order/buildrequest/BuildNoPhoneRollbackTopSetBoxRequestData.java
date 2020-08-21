package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonerollbacktopsetbox.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonerollbacktopsetbox.order.requestdata.NoPhoneRollbackTopSetBoxRequestData;

public class BuildNoPhoneRollbackTopSetBoxRequestData extends BaseBuilder implements IBuilder{

	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
		NoPhoneRollbackTopSetBoxRequestData data=(NoPhoneRollbackTopSetBoxRequestData)brd;
		data.setSerialNumber(param.getString("SERIAL_NUMBER"));
    }
	
	public BaseReqData getBlankRequestDataInstance()
    {
        return new NoPhoneRollbackTopSetBoxRequestData();
    }
}
