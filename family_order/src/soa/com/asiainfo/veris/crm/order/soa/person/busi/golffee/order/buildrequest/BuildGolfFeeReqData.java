
package com.asiainfo.veris.crm.order.soa.person.busi.golffee.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.golffee.order.requestdata.GolfFeeReqData;

public class BuildGolfFeeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new GolfFeeReqData();
    }

}
