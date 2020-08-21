
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import com.ailk.common.data.IDataset;

public class CreateDesktopTelGroupMemberReqData extends CreateGroupMemberReqData
{
    private String power = "";

    private IDataset shorts;

    private String ROLE_SHORT; // 角色|短号

    public String getPower()
    {
        return power;
    }

    public String getROLE_SHORT()
    {
        return ROLE_SHORT;
    }

    public IDataset getShorts()
    {
        return shorts;
    }

    public void setPower(String power)
    {
        this.power = power;
    }

    public void setROLE_SHORT(String role_short)
    {
        ROLE_SHORT = role_short;
    }

    public void setShorts(IDataset shorts)
    {
        this.shorts = shorts;
    }
}
