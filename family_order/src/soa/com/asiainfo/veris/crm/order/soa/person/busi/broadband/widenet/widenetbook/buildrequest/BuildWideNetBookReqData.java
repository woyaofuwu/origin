
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook.requsetdata.WideNetBookReqData;

public class BuildWideNetBookReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	WideNetBookReqData war= (WideNetBookReqData)brd;
    	war.setRemark(param.getString("rmark"));//备注
    	war.setValidCode(param.getString("validCode"));//验证码
    }

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new WideNetBookReqData();
	}



}
