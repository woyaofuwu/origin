
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.requestdata.MultiSNOneCardRequestData;

public class BuildMultiSNOneCardRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        MultiSNOneCardRequestData reqData = (MultiSNOneCardRequestData) brd;

        reqData.setOper_type(data.getString("OPER_TYPE"));
        reqData.setService_type(data.getString("SERVICE_TYPE"));
        reqData.setDeputy_sn_input(data.getString("DEPUTY_SN_INPUT"));
        reqData.setCooper_area(data.getString("COOPER_AREA"));
        reqData.setCooper_net(data.getString("COOPER_NET"));
        reqData.setMax_fee(data.getString("MAX_FEE"));
        reqData.setSum_fee(data.getString("SUM_FEE"));
        reqData.setAuth_serial_number(data.getString("AUTH_SERIAL_NUMBER"));
        reqData.setVaild_date(data.getString("VAILD_DATE"));
        reqData.setInvaild_date(data.getString("INVAILD_DATE"));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new MultiSNOneCardRequestData();
    }

}
