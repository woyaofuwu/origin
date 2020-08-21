package com.asiainfo.veris.crm.order.soa.person.busi.mobileSpecialepay.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.mobileSpecialepay.order.requestdata.MobileSpecialepayReqData;

public class BuildMobileSpecialepayReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
    	MobileSpecialepayReqData reqData = (MobileSpecialepayReqData) brd;

        String PAY_MONEY = param.getString("PAY_MONEY");
        reqData.setPAY_MONEY(PAY_MONEY);

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new MobileSpecialepayReqData();
    }

}
