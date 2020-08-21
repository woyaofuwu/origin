
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.requestdata.MultiSNOneCardRequestData;

public class BuildRemedyCardRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        MultiSNOneCardRequestData reqData = (MultiSNOneCardRequestData) brd;

        reqData.setOper_type(data.getString("OPERATION_TYPE"));
        reqData.setAuth_serial_number(data.getString("SERIAL_NUMBER"));
        reqData.setSerialNumberO(data.getString("SERIAL_NUMBER_O", ""));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new MultiSNOneCardRequestData();
    }

}
