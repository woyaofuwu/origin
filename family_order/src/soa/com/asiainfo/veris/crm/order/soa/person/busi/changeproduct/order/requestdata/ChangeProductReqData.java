
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

public class ChangeProductReqData extends BaseChangeProductReqData
{
    private String oldVpmnDiscnt;

    private String newVpmnDisnct;

    private String invoiceCode;

    // 新增国际长途服务时,此值为19为同时添加国际漫游服务标识
    // 两城一家、非常假期删除的元素
    private String optionParam1;

    public String getInvoiceCode()
    {
        return invoiceCode;
    }

    public String getNewVpmnDisnct()
    {
        return newVpmnDisnct;
    }

    public String getOldVpmnDiscnt()
    {
        return oldVpmnDiscnt;
    }

    public String getOptionParam1()
    {
        return optionParam1;
    }

    public void setInvoiceCode(String invoiceCode)
    {
        this.invoiceCode = invoiceCode;
    }

    public void setNewVpmnDisnct(String newVpmnDisnct)
    {
        this.newVpmnDisnct = newVpmnDisnct;
    }

    public void setOldVpmnDiscnt(String oldVpmnDiscnt)
    {
        this.oldVpmnDiscnt = oldVpmnDiscnt;
    }

    public void setOptionParam1(String optionParam1)
    {
        this.optionParam1 = optionParam1;
    }
}
