/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * 
 * @author chenzg
 *
 */
public class PersonGrpUserUnionPayReqData extends BaseReqData
{
    private List<PersonGrpUserUnionPayMemberData> memberList;
    private String endDate;// for UU relation
    private String startCycId;// for payreLation
    private String xTag;// 发起类型:1-主号发起；2-副号发起
    private String intfFlag;// for 接口
    private String modifyTag;// for 接口
    private String groupPack;
    public String getEndDate()
    {
        return endDate;
    }

    public String getIntfFlag()
    {
        return intfFlag;
    }

    public List<PersonGrpUserUnionPayMemberData> getMemberList()
    {
        return memberList;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getStartCycId()
    {
        return startCycId;
    }

    public String getxTag()
    {
        return xTag;
    }
    
    public String getGroupPack() {
		return groupPack;
	}

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setIntfFlag(String intfFlag)
    {
        this.intfFlag = intfFlag;
    }

    public void setMemberList(List<PersonGrpUserUnionPayMemberData> memberList)
    {
        this.memberList = memberList;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setStartCycId(String startCycId)
    {
        this.startCycId = startCycId;
    }

    public void setxTag(String xTag)
    {
        this.xTag = xTag;
    }

	public void setGroupPack(String groupPack) {
		this.groupPack = groupPack;
	}
    

}
