
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

public class BuildChangeProduct extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest.BuildChangeProduct implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        super.buildBusiRequestData(param, brd);

        ChangeProductReqData request = (ChangeProductReqData) brd;

        request.setOldVpmnDiscnt(param.getString("OLD_VPMN_DISCNT"));
        request.setNewVpmnDisnct(param.getString("NEW_VPMN_DISCNT"));
        request.setInvoiceCode(param.getString("INVOICE_CODE"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ChangeProductReqData();
    }
}
