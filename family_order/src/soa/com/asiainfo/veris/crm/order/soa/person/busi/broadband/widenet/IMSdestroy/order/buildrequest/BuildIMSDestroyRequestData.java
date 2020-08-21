
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.IMSdestroy.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.IMSdestroy.order.requestdata.IMSDestroyRequestData;

public class BuildIMSDestroyRequestData extends BaseBuilder implements IBuilder
{
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
    	IMSDestroyRequestData reqData = (IMSDestroyRequestData) brd;
        reqData.setRemoveReason(data.getString("REMOVE_REASON", "")); // 销户原因
        String wideSerialNumber = data.getString("WIDE_SERIAL_NUMBER","");  //宽带号码
        String serialNumber = data.getString("SERIAL_NUMBER");    //固话号码
        reqData.setWideSerialNumber(wideSerialNumber);
        reqData.setSerialNumber(serialNumber);
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new IMSDestroyRequestData();
    }

}
