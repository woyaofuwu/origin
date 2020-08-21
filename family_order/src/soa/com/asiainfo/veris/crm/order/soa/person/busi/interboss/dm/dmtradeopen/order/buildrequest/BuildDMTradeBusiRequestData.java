
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradeopen.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradeopen.order.requestdata.DMTradeBusiRequestData;

public class BuildDMTradeBusiRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        DMTradeBusiRequestData requestData = (DMTradeBusiRequestData) brd;
        requestData.setSerialNum(data.getString("SERIAL_NUMBER", ""));
        requestData.setResCode(data.getString("RES_CODE", ""));
        requestData.setUpdateTag(data.getString("UPDATE_TAG", ""));
        requestData.setDMTag(data.getString("DM_TAG", ""));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new DMTradeBusiRequestData();
    }

}
