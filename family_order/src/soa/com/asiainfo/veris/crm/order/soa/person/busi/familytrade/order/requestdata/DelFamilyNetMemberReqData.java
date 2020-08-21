
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class DelFamilyNetMemberReqData extends BaseReqData
{

    private List<FamilyMemberData> mebUcaList = new ArrayList<FamilyMemberData>();

    private String effectNow = "NO";// 是否立即终止

    private String isInterface = "0";// 是否接口 默认0：不是 1：是

    public void addMebUca(FamilyMemberData mebUca)
    {
        this.mebUcaList.add(mebUca);
    }

    public String getEffectNow()
    {
        return effectNow;
    }

    public String getIsInterface()
    {
        return isInterface;
    }

    public List<FamilyMemberData> getMebUcaList()
    {
        return mebUcaList;
    }

    public void setEffectNow(String effectNow)
    {
        this.effectNow = effectNow;
    }

    public void setIsInterface(String isInterface)
    {
        this.isInterface = isInterface;
    }

    public void setMebUcaList(List<FamilyMemberData> mebUcaList)
    {
        this.mebUcaList = mebUcaList;
    }

}
