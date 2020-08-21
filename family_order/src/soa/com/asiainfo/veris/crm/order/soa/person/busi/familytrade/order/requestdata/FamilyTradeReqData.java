
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class FamilyTradeReqData extends BaseReqData
{

    private List<MemberData> memberDataList = new ArrayList<MemberData>();

    public void addMemberData(MemberData memberData)
    {
        this.memberDataList.add(memberData);
    }

    public List<MemberData> getMemberDataList()
    {
        return memberDataList;
    }

    public void setMemberDataList(List<MemberData> memberDataList)
    {
        this.memberDataList = memberDataList;
    }
}
