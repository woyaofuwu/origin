
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changesvcstate.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BuildFixTelChangeSvcStateReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        brd.setRemark(param.getString("REMARK"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {

        return new BaseReqData();
    }

}
