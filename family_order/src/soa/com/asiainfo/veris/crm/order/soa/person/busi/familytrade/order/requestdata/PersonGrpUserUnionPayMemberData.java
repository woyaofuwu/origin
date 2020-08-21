package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 
 * @author chenzg
 *
 */
public class PersonGrpUserUnionPayMemberData
{
    private UcaData uca;// 成员三户资料
    private String memberSn;
    private String modifyTag;
    private String StartCycleId;
    public String getMemberSn()
    {
        return memberSn;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getStartCycleId()
    {
        return StartCycleId;
    }

    public UcaData getUca()
    {
        return uca;
    }

    public void setMemberSn(String memberSn)
    {
        this.memberSn = memberSn;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setStartCycleId(String startCycleId)
    {
        StartCycleId = startCycleId;
    }

    public void setUca(UcaData uca)
    {
        this.uca = uca;
    }

    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return this.memberSn + "|" + this.modifyTag + "|" + this.StartCycleId;
    }

}
