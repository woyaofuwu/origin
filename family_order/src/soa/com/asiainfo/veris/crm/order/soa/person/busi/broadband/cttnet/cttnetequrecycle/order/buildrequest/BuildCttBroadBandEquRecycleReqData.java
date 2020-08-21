
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetequrecycle.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetequrecycle.order.requestdata.CttBroadBandEquRecycleReqData;

public class BuildCttBroadBandEquRecycleReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CttBroadBandEquRecycleReqData cttReqData = (CttBroadBandEquRecycleReqData) brd;

        cttReqData.setRES_CODE(param.getString("RES_CODE"));
        cttReqData.setRES_KIND_CODE(param.getString("RES_KIND_CODE"));
        cttReqData.setRES_RENT_TYPE(param.getString("RES_RENT_TYPE"));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttBroadBandEquRecycleReqData();
    }

}
