
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData; 

public class CPEActiveReqData extends BaseReqData
{
	private List<CPEActiveMemberData> memberList;

    private String endDate;// for UU relation

    private String startCycId;// for payreLation

    private String xTag;// 发起类型:1-主号发起；2-副号发起

    private String intfFlag;// for 接口

    private String modifyTag;// for 接口
    
    private String equipNum;
    private String firstTimeTag;

    public String getEndDate()
    {
        return endDate;
    }

    public String getIntfFlag()
    {
        return intfFlag;
    }

    public List<CPEActiveMemberData> getMemberList()
    {
        return memberList;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }
    
    public String getEquipNum()
    {
        return equipNum;
    }
    
    public String getFirstTimeTag()
    {
        return firstTimeTag;
    }

    public String getStartCycId()
    {
        return startCycId;
    }

    public String getxTag()
    {
        return xTag;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setIntfFlag(String intfFlag)
    {
        this.intfFlag = intfFlag;
    }

    public void setMemberList(List<CPEActiveMemberData> memberList)
    {
        this.memberList = memberList;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }
    
    public void setEquipNum(String equipNum)
    {
        this.equipNum = equipNum;
    }
    
    public void setFirstTimeTag(String firstTimeTag)
    {
        this.firstTimeTag = firstTimeTag;
    }

    public void setStartCycId(String startCycId)
    {
        this.startCycId = startCycId;
    }

    public void setxTag(String xTag)
    {
        this.xTag = xTag;
    }
}
