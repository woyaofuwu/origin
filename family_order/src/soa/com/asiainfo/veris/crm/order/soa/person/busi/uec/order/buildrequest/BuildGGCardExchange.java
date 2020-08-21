
package com.asiainfo.veris.crm.order.soa.person.busi.uec.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.uec.order.requestdata.GGCardExchangeReqData;

public class BuildGGCardExchange extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        GGCardExchangeReqData reqData = (GGCardExchangeReqData) brd;
        reqData.setSerialNumber(param.getString("SERIAL_NUMBER"));
        reqData.setSerialNumber_b(param.getString("SERIAL_NUMBER_B"));
        reqData.setCardTypecode(param.getString("CARD_TYPE_CODE"));
        reqData.setCardNo(param.getString("CARD_NO"));
        reqData.setParacode1(param.getInt("PARA_CODE1"));
        reqData.setParacode2(param.getInt("PARA_CODE2"));
        reqData.setChannelId(param.getString("CHANNEL_ID"));
        reqData.setPaymentId(param.getString("PAYMENT_ID"));
        reqData.setPayfeeModecode(param.getString("PAY_FEE_MODE_CODE"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new GGCardExchangeReqData();
    }

}
