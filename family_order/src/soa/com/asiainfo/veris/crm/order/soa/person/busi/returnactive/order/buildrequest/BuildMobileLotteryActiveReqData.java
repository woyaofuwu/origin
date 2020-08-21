
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.MobileLotteryActiveReqData;

public class BuildMobileLotteryActiveReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        MobileLotteryActiveReqData reqData = (MobileLotteryActiveReqData) brd;

        reqData.setLot_month(param.getString("MONTH"));
        reqData.setRemark(param.getString("REMARK"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new MobileLotteryActiveReqData();
    }

}
