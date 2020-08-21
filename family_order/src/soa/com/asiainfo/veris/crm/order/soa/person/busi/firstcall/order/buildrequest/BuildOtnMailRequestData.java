
package com.asiainfo.veris.crm.order.soa.person.busi.firstcall.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.firstcall.order.requestdata.OtnMailRequestData;

public class BuildOtnMailRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        OtnMailRequestData otnMailReqData = (OtnMailRequestData) brd;
        otnMailReqData.setElementId(param.getString("ELEMENT_ID"));
        otnMailReqData.setUserId(param.getString("USER_ID"));
        otnMailReqData.setSerialNumber(param.getString("SERIAL_NUMBER"));
        otnMailReqData.setPackageId(param.getString("PACKAGE_ID"));
        otnMailReqData.setProductId(param.getString("PRODUCT_ID"));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new OtnMailRequestData();
    }

}
