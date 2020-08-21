
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser;

public class DestroyMasGroupUserReqData extends DestroyGroupUserReqData
{
    private String syndiscntFlag;// 是否同步优惠编码

    public void getSyndiscntFlag(String syndiscntFlag)
    {
        this.syndiscntFlag = syndiscntFlag;
    }

    public void setSyndiscntFlag(String syndiscntFlag)
    {
        this.syndiscntFlag = syndiscntFlag;
    }
}
