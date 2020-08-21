
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.ReOpenGPRSReqData;

public class BuildReOpenGPRS extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData inParam, BaseReqData brd) throws Exception
    {

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ReOpenGPRSReqData();
    }

}
