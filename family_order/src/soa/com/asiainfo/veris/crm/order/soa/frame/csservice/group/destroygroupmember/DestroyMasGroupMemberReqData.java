
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember;

public class DestroyMasGroupMemberReqData extends DestroyGroupMemberReqData
{
    private String ROLE_CODE_B; // 成员角色

    public String getRoleCodeB()
    {
        return ROLE_CODE_B;
    }

    public void setRoleCodeB(String roleCodeB)
    {
        ROLE_CODE_B = roleCodeB;
    }
}
