
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.integralacctref.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.integralacctref.order.requestdata.IntegralAcctRefRequestData;

public class BuildIntegralAcctRefRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        IntegralAcctRefRequestData reqData = (IntegralAcctRefRequestData) brd;
        reqData.setRefAcctId(param.getString("REF_INTEGRAL_ACCT_ID"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new IntegralAcctRefRequestData();
    }

}
