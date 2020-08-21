
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class VipSimBakActReqData extends BaseReqData
{
    private String newSimCardNo;

    private String newKI;

    private String newIMSI;

    private String oldResCode;

    private String oldIMSI;

    private String oldStartDate;

    private String simCardNo2;

    private String identCode;

    private String opc;

    private String oldOpc;

    public String getIdentCode()
    {
        return identCode;
    }

    public String getNewIMSI()
    {
        return newIMSI;
    }

    public String getNewKI()
    {
        return newKI;
    }

    public String getNewSimCardNo()
    {
        return newSimCardNo;
    }

    public String getOldIMSI()
    {
        return oldIMSI;
    }

    public String getOldOpc()
    {
        return oldOpc;
    }

    public String getOldResCode()
    {
        return oldResCode;
    }

    public String getOldStartDate()
    {
        return oldStartDate;
    }

    public String getOpc()
    {
        return opc;
    }

    public String getSimCardNo2()
    {
        return simCardNo2;
    }

    public void setIdentCode(String identCode)
    {
        this.identCode = identCode;
    }

    public void setNewIMSI(String newIMSI)
    {
        this.newIMSI = newIMSI;
    }

    public void setNewKI(String newKI)
    {
        this.newKI = newKI;
    }

    public void setNewSimCardNo(String newSimCardNo)
    {
        this.newSimCardNo = newSimCardNo;
    }

    public void setOldIMSI(String oldIMSI)
    {
        this.oldIMSI = oldIMSI;
    }

    public void setOldOpc(String oldOpc)
    {
        this.oldOpc = oldOpc;
    }

    public void setOldResCode(String oldResCode)
    {
        this.oldResCode = oldResCode;
    }

    public void setOldStartDate(String oldStartDate)
    {
        this.oldStartDate = oldStartDate;
    }

    public void setOpc(String opc)
    {
        this.opc = opc;
    }

    public void setSimCardNo2(String simCardNo2)
    {
        this.simCardNo2 = simCardNo2;
    }

}
