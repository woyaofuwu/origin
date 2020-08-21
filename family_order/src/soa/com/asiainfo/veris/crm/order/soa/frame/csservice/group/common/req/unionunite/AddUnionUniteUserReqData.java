
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req.unionunite;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMemberReqData;

public class AddUnionUniteUserReqData extends CreateGroupMemberReqData
{
    private String mebEparchyCode;

    private String roleCodeB;

    private String ifGrpAcct;

    private String relationTypeCode;

    private String bankCode;

    private String bankAcctNo;

    private String payModeCode;

    public String getBankAcctNo()
    {
        return bankAcctNo;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public String getIfGrpAcct()
    {
        return ifGrpAcct;
    }

    public String getMebEparchyCode()
    {
        return mebEparchyCode;
    }

    public String getPayModeCode()
    {
        return payModeCode;
    }

    public String getRelationTypeCode()
    {
        return relationTypeCode;
    }

    public String getRoleCodeB()
    {
        return roleCodeB;
    }

    public void setBankAcctNo(String bankAcctNo)
    {
        this.bankAcctNo = bankAcctNo;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public void setIfGrpAcct(String ifGrpAcct)
    {
        this.ifGrpAcct = ifGrpAcct;
    }

    public void setMebEparchyCode(String mebEparchyCode)
    {
        this.mebEparchyCode = mebEparchyCode;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setRelationTypeCode(String relationTypeCode)
    {
        this.relationTypeCode = relationTypeCode;
    }

    public void setRoleCodeB(String roleCodeB)
    {
        this.roleCodeB = roleCodeB;
    }

}
