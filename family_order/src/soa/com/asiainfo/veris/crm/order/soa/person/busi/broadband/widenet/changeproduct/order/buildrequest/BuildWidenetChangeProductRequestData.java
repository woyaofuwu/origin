
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.requestdata.WidenetProductRequestData;

public class BuildWidenetChangeProductRequestData extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest.BuildChangeProduct implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        super.buildBusiRequestData(param, brd);

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new WidenetProductRequestData();
    }
}
