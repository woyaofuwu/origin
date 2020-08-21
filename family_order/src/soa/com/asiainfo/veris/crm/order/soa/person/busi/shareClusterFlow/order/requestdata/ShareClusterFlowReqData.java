
package com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ShareClusterFlowReqData extends BaseReqData
{

    private List<MemberData> memberDataList = new ArrayList<MemberData>();

    private String MemberCancel;// 是否为副卡取消
    
    private String cancelTag;// 是否主卡取消
    
    private String isExist;// 是否主卡创建

    public void addMemberData(MemberData memberData)
    {
        this.memberDataList.add(memberData);
    }

    public String getMemberCancel()
    {
        return MemberCancel;
    }

    public List<MemberData> getMemberDataList()
    {
        return memberDataList;
    }

    public void setMemberCancel(String MemberCancel)
    {
        this.MemberCancel = MemberCancel;
    }

    public void setMemberDataList(List<MemberData> memberDataList)
    {
        this.memberDataList = memberDataList;
    }
    
    public String getCancelTag() {
		return cancelTag;
	}
    
    public void setCancelTag(String cancelTag) {
		this.cancelTag = cancelTag;
	}
    
    public String getIsExist() {
		return isExist;
	}
    
    public void setIsExist(String isExist) {
		this.isExist = isExist;
	}
}
