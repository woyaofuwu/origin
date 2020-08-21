
package com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElementReqData;

public class LargessFluxAddDiscntReqData extends ChangeMemElementReqData
{
    private String DISCNT_CODE;

    private String bindTeam;
    
    private String limitFee;
    
    private String GRP_USER_ID;
    
    private String GRP_SERIAL_NUMBER;
    
    public String getDISCNT_CODE()
    {
        return DISCNT_CODE;
    }

    public void setDISCNT_CODE(String discntCode)
    {
        DISCNT_CODE = discntCode;
    }

    public String getBindTeam()
    {
        return bindTeam;
    }

    public void setBindTeam(String bindTeam)
    {
        this.bindTeam = bindTeam;
    }

    public String getLimitFee()
    {
        return limitFee;
    }

    public void setLimitFee(String limitFee)
    {
        this.limitFee = limitFee;
    }

    public String getGRP_USER_ID()
    {
        return GRP_USER_ID;
    }

    public void setGRP_USER_ID(String grp_user_id)
    {
        GRP_USER_ID = grp_user_id;
    }

    public String getGRP_SERIAL_NUMBER()
    {
        return GRP_SERIAL_NUMBER;
    }

    public void setGRP_SERIAL_NUMBER(String grp_serial_number)
    {
        GRP_SERIAL_NUMBER = grp_serial_number;
    }
    
}
