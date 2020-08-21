
package com.asiainfo.veris.crm.order.soa.group.changevpmnscpcode;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElementReqData;

public class ChangeVPMNScpCodeReqData extends ChangeUserElementReqData
{
    private String OLD_SCP_CODE;

    private String NEW_SCP_CODE;

    public String getOLD_SCP_CODE()
    {
        return OLD_SCP_CODE;
    }

    public void setOLD_SCP_CODE(String oLD_SCP_CODE)
    {
        OLD_SCP_CODE = oLD_SCP_CODE;
    }

    public String getNEW_SCP_CODE()
    {
        return NEW_SCP_CODE;
    }

    public void setNEW_SCP_CODE(String nEW_SCP_CODE)
    {
        NEW_SCP_CODE = nEW_SCP_CODE;
    }

}
