
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.DestroyFamilyReqData;

public class BuildDestroyFamilyReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new DestroyFamilyReqData();
    }

}
