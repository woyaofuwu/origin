
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.resetaccount.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.resetaccount.order.requestdata.ResetAccountRequestData;

public class BuildResetAccountRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ResetAccountRequestData reqData = (ResetAccountRequestData) brd;
        reqData.setSerialNumber(param.getString("SERIAL_NUMBER"));

    }

    public BaseReqData getBlankRequestDataInstance()
    {

        return new ResetAccountRequestData();
    }

}
