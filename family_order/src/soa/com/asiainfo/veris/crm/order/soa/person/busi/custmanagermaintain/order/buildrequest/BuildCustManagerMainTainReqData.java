
package com.asiainfo.veris.crm.order.soa.person.busi.custmanagermaintain.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.custmanagermaintain.order.requestdata.CustManagerMainTainReqData;

public class BuildCustManagerMainTainReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        CustManagerMainTainReqData reqData = (CustManagerMainTainReqData) brd;
        reqData.setIS_MES_TAG(data.getString(""));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new CustManagerMainTainReqData();
    }

}
