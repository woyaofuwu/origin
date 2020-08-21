
package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.changeproduct.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

public class ChangeProductReqData extends BaseChangeProductReqData
{
    private String oldVpmnDiscnt;

    private String newVpmnDisnct;

    private String invoiceCode;

    private String rsrvStr1;// 新增国际长途服务时,此值为19为同时添加国际漫游服务标识【可能】

    private String rsrvStr2;//

    private String rsrvStr3;//

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

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
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

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }
}
