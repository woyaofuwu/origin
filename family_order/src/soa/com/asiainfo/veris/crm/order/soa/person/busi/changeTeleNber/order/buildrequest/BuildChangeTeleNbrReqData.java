
package com.asiainfo.veris.crm.order.soa.person.busi.changeTeleNber.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeTeleNber.order.requestdata.ChangeTeleNbrReqData;

public class BuildChangeTeleNbrReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData idata, BaseReqData basereqdata) throws Exception
    {
        // TODO Auto-generated method stub
        ChangeTeleNbrReqData reqData = (ChangeTeleNbrReqData) basereqdata;
        reqData.setNewSerialNumber(idata.getString("NEW_SERIAL_NUMBER"));
        reqData.setChangteleNotice(idata.getString("CHANGE_NUMBER_MONTH"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ChangeTeleNbrReqData();
    }

}
