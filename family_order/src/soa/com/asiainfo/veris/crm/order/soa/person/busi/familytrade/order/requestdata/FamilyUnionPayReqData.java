/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-5-22 修改历史 Revision 2014-5-22 上午09:26:25
 */
public class FamilyUnionPayReqData extends BaseReqData
{

    private List<UnionPayMemberData> memberList;

    private String endDate;// for UU relation

    private String startCycId;// for payreLation

    private String xTag;// 发起类型:1-主号发起；2-副号发起

    private String intfFlag;// for 接口

    private String modifyTag;// for 接口
    
    private String endDate_flag; //结束到上个月的标记k3

    private String payitemCode; //指定统付业务

    public String getEndDate_flag() {
		return endDate_flag;
	}

	public void setEndDate_flag(String endDate_flag) {
		this.endDate_flag = endDate_flag;
	}

    public String getEndDate()
    {
        return endDate;
    }

    public String getIntfFlag()
    {
        return intfFlag;
    }

    public List<UnionPayMemberData> getMemberList()
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

    public String getPayitemCode()
    {
        return payitemCode;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setIntfFlag(String intfFlag)
    {
        this.intfFlag = intfFlag;
    }

    public void setMemberList(List<UnionPayMemberData> memberList)
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

    public void setPayitemCode(String payitemCode)
    {
        this.payitemCode = payitemCode;
    }


}
