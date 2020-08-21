
package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ShareMealReqData extends BaseReqData
{

    private List<MemberData> memberDataList = new ArrayList<MemberData>();

    private String MemberCancel;// 是否为副卡取消

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
}
