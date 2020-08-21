
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class VipSimBakCancelReqData extends BaseReqData
{
    private String newSimCardNo;

    private String newKI;

    private String newIMSI;

    private String normalUserSimbak;

    private String vipEmptyTag;

    private String oldSimCardNo;

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

    public String getNormalUserSimbak()
    {
        return normalUserSimbak;
    }

    public String getOldSimCardNo()
    {
        return oldSimCardNo;
    }

    public String getVipEmptyTag()
    {
        return vipEmptyTag;
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

    public void setNormalUserSimbak(String normalUserSimbak)
    {
        this.normalUserSimbak = normalUserSimbak;
    }

    public void setOldSimCardNo(String oldSimCardNo)
    {
        this.oldSimCardNo = oldSimCardNo;
    }

    public void setVipEmptyTag(String vipEmptyTag)
    {
        this.vipEmptyTag = vipEmptyTag;
    }
}
