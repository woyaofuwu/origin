
package com.asiainfo.veris.crm.order.soa.person.busi.score.getgiftofstudyimbursement.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.getgiftofstudyimbursement.order.requestdata.GetGiftOfStudyImbursementRequestData;

public class BuildGiftOfStudyRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        GetGiftOfStudyImbursementRequestData reqData = (GetGiftOfStudyImbursementRequestData) brd;

        reqData.setSerialNumber(param.getString("SERIAL_NUMBER"));
        reqData.setGiftsInfo(param.getString("IDATA"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new GetGiftOfStudyImbursementRequestData();
    }

}
