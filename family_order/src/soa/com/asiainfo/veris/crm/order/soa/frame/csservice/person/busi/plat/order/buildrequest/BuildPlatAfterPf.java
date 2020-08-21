
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatAfterPfReqData;

public class BuildPlatAfterPf extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        PlatAfterPfReqData cpReqData = (PlatAfterPfReqData) brd;

        cpReqData.setPassId(data.getString("FIELDS1"));
        cpReqData.setTradeId(data.getString("TRADE_ID"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new PlatAfterPfReqData();
    }
}
