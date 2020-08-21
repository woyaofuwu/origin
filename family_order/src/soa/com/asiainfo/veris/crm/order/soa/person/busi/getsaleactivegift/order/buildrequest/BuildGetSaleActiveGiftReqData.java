
package com.asiainfo.veris.crm.order.soa.person.busi.getsaleactivegift.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.getsaleactivegift.order.requestdata.GetSaleActiveGiftReqData;

public class BuildGetSaleActiveGiftReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        GetSaleActiveGiftReqData gsagrd = (GetSaleActiveGiftReqData) brd;
        String[] giftInfo = param.getString("GIFT_CODE").split(",");
        String resCode = "", relationTradeId = "";
        if (giftInfo.length > 1)
        {
            resCode = giftInfo[0];
            relationTradeId = giftInfo[1];
        }
        gsagrd.setResCode(resCode);
        gsagrd.setRelationTradeId(relationTradeId);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new GetSaleActiveGiftReqData();
    }

}
