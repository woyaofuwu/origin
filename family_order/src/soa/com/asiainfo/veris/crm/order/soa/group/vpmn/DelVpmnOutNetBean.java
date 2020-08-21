
package com.asiainfo.veris.crm.order.soa.group.vpmn;

public class DelVpmnOutNetBean extends DelParentVpmnOutNetBean
{

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "3583";
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3583";
    }
}
