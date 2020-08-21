
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.requestdata.IntegralPlanAddReqData;

public class BuildIntegralPlanAddReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        IntegralPlanAddReqData reqData = (IntegralPlanAddReqData) brd;
        String integralPlanId = param.getString("INTEGRAL_PLAN_ID");
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
		
        reqData.setIntegralPlanId(integralPlanId);
        reqData.setStartDate(startDate);
        reqData.setEndDate(endDate);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new IntegralPlanAddReqData();
    }

}
