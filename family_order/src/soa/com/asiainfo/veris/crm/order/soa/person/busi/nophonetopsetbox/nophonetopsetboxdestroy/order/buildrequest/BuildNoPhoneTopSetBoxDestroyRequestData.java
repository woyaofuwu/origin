package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxdestroy.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxdestroy.order.requestdata.NoPhoneTopSetBoxDestroyRequestData;

public class BuildNoPhoneTopSetBoxDestroyRequestData extends BaseBuilder implements IBuilder{

	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
		NoPhoneTopSetBoxDestroyRequestData data=(NoPhoneTopSetBoxDestroyRequestData)brd;
		data.setSerialNumber(param.getString("SERIAL_NUMBER"));
		data.setIsReturnTopsetBox(param.getString("IS_RETURN_TOPSETBOX",""));
		data.setIsMergeWideCancel(param.getString("IS_MERGE_WIDE_CANCEL","0"));
    }
	
	public BaseReqData getBlankRequestDataInstance()
    {
        return new NoPhoneTopSetBoxDestroyRequestData();
    }
}
