
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BuildSpecSvcStateReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData idata, BaseReqData basereqdata) throws Exception
    {

    }

    /**
     * 不走任何规则
     */
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new BaseReqData();
    }
}
