
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req.unionunite;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMemberReqData;

public class DelUnionUniteUserReqData extends CreateGroupMemberReqData
{

    private String mebEparchyCode;

    private String relationTypeCode;

    public String getMebEparchyCode()
    {
        return mebEparchyCode;
    }

    public String getRelationTypeCode()
    {
        return relationTypeCode;
    }

    public void setMebEparchyCode(String mebEparchyCode)
    {
        this.mebEparchyCode = mebEparchyCode;
    }

    public void setRelationTypeCode(String relationTypeCode)
    {
        this.relationTypeCode = relationTypeCode;
    }

}
