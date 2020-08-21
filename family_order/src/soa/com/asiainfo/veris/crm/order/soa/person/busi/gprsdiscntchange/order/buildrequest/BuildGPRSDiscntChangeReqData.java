
package com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange.order.requestdata.GPRSDiscntChangeReqData;

public class BuildGPRSDiscntChangeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        GPRSDiscntChangeReqData gdcrd = (GPRSDiscntChangeReqData) brd;
        gdcrd.setNewDiscntCode(param.getString("DISCNT_CODE"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new GPRSDiscntChangeReqData();
    }

}
