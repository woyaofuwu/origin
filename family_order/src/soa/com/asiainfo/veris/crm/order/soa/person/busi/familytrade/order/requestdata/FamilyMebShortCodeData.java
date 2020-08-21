
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class FamilyMebShortCodeData
{

    private UcaData uca;

    private String oldShortCode;

    private String newShortCode;

    public String getNewShortCode()
    {
        return newShortCode;
    }

    public String getOldShortCode()
    {
        return oldShortCode;
    }

    public UcaData getUca()
    {
        return uca;
    }

    public void setNewShortCode(String newShortCode)
    {
        this.newShortCode = newShortCode;
    }

    public void setOldShortCode(String oldShortCode)
    {
        this.oldShortCode = oldShortCode;
    }

    public void setUca(UcaData uca)
    {
        this.uca = uca;
    }
}
