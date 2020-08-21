
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElementReqData;

public class ChangeVpnSpecDiscntReqData extends ChangeMemElementReqData
{
    private String DISCNT_CODE;

    private boolean has655;

    public boolean getHas655()
    {
        return has655;
    }

    public void setHas655(boolean has655)
    {
        this.has655 = has655;
    }

    public String getDISCNT_CODE()
    {
        return DISCNT_CODE;
    }

    public void setDISCNT_CODE(String dISCNT_CODE)
    {
        DISCNT_CODE = dISCNT_CODE;
    }

}
