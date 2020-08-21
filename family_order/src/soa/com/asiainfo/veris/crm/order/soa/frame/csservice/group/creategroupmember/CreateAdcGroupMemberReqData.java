
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

public class CreateAdcGroupMemberReqData extends CreateGroupMemberReqData
{
    private String synDiscntFlag;// 是否同步优惠

    private String xxtName; // 校讯通名称

    private String xxtPhone; // 校讯通手机号

    private String familyNumber; // 学护卡家长号码

    public String getSynDiscntFlag()
    {
        return synDiscntFlag;
    }

    public String getXxtName()
    {
        return xxtName;
    }

    public String getXxtPhone()
    {
        return xxtPhone;
    }

    public void setSynDiscntFlag(String synDiscntFlag)
    {
        this.synDiscntFlag = synDiscntFlag;
    }

    public void setXxtName(String xxtName)
    {
        this.xxtName = xxtName;
    }

    public void setXxtPhone(String xxtPhone)
    {
        this.xxtPhone = xxtPhone;
    }

    public String getFamilyNumber()
    {
        return familyNumber;
    }

    public void setFamilyNumber(String familyNumber)
    {
        this.familyNumber = familyNumber;
    }



}
