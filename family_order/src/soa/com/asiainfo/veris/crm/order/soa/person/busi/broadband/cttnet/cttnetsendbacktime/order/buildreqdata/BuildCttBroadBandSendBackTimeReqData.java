
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetsendbacktime.order.buildreqdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetsendbacktime.order.requestdata.CttBroadBandSendBackTimeReqData;

public class BuildCttBroadBandSendBackTimeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CttBroadBandSendBackTimeReqData reqData = (CttBroadBandSendBackTimeReqData) brd;
        reqData.setSend_back_days(param.getString("SEND_BACK_DAYS"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttBroadBandSendBackTimeReqData();
    }
}
