package com.asiainfo.veris.crm.iorder.soa.family.busi.destroy.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.destroy.order.requestdata.FamilyDestroyReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.buildrequest.BuildBaseFamilyBusiReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BuildFamilyDestroy extends BuildBaseFamilyBusiReqData implements IBuilder
{
	@Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
		FamilyDestroyReqData reqData = (FamilyDestroyReqData)brd;
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyDestroyReqData();
    }
    
}
