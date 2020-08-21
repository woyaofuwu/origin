
package com.asiainfo.veris.crm.order.soa.person.busi.replacecard.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.replacecard.order.requestdata.ReplaceCardReqData;

public class BuildReplaceCardReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	ReplaceCardReqData reqData=(ReplaceCardReqData)brd;
    	reqData.setSimCardNo(param.getString("SIM_CARD_NO"));
    	reqData.setImsi(param.getString("IMSI"));
    	reqData.setEmptyCardId(param.getString("EMPTY_CARD_ID"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ReplaceCardReqData();
    }

}
