/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * @CREATED by gongp@2014-6-18 修改历史 Revision 2014-6-18 上午10:52:09
 */
public class CPEActiveMemberData
{
    private UcaData uca;// 成员三户资料

    private String memberSn;

    private String modifyTag;

    private String StartCycleId;
    
    private String equipNum;
    private String firstTimeTag;
    
    public String getEquipNum()
    {
        return equipNum;
    }
    
    public String getFirstTimeTag()
    {
        return firstTimeTag;
    }
    
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
    
    public void setEquipNum(String equipNum)
    {
        this.equipNum = equipNum;
    }
    
    public void setFirstTimeTag(String firstTimeTag)
    {
        this.firstTimeTag = firstTimeTag;
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
