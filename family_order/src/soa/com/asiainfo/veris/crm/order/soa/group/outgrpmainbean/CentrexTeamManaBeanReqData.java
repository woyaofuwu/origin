
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class CentrexTeamManaBeanReqData extends GroupReqData
{
    private String teamType = "2"; // 组类型

    private String huntType; // 寻呼组类型

    private String accessCode; // 接入码

    private String memNumber = ""; // 成员号码

    private String operCode = "0"; // 操作方式 0新增 1删除 2修改

    private String teamName = "0"; // 组名

    private String memTeam; // 成员组

    private String teamserial;

    private String team_id;

    private IData MEBUSERINFO;

    public String getAccessCode()
    {
        return accessCode;
    }

    public String getMemNumber()
    {
        return memNumber;
    }

    public String getHuntType()
    {
        return huntType;
    }

    public String getMemTeam()
    {
        return memTeam;
    }

    public IData getMEBUSERINFO()
    {
        return MEBUSERINFO;
    }

    public String getOperCode()
    {
        return operCode;
    }

    public String getTeam_id()
    {
        return team_id;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public String getTeamserial()
    {
        return teamserial;
    }

    public String getTeamType()
    {
        return teamType;
    }

    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }

    public void setMemNumber(String memNumber)
    {
        this.memNumber = memNumber;
    }

    public void setHuntType(String huntType)
    {
        this.huntType = huntType;
    }

    public void setMemTeam(String memTeam)
    {
        this.memTeam = memTeam;
    }

    public void setMEBUSERINFO(IData mebuserinfo)
    {
        MEBUSERINFO = mebuserinfo;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public void setTeam_id(String team_id)
    {
        this.team_id = team_id;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public void setTeamserial(String teamserial)
    {
        this.teamserial = teamserial;
    }

    public void setTeamType(String teamType)
    {
        this.teamType = teamType;
    }
}
