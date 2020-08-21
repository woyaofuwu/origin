
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

public class CreateAdcGroupUserReqData extends CreateGroupUserReqData
{
    private String synDiscntFlag;// 是否同步优惠

    private String hireFee;// 设备租赁费

    private String feeCycle;// 计费周期

    private String rsrvStr9;// 

    public String getFeeCycle()
    {
        return feeCycle;
    }

    public String getHireFee()
    {
        return hireFee;
    }

    public String getRsrvStr9()
    {
        return rsrvStr9;
    }

    public String getSynDiscntFlag()
    {
        return synDiscntFlag;
    }

    public void setFeeCycle(String feeCycle)
    {
        this.feeCycle = feeCycle;
    }

    public void setHireFee(String hireFee)
    {
        this.hireFee = hireFee;
    }

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
    }

    public void setSynDiscntFlag(String synDiscntFlag)
    {
        this.synDiscntFlag = synDiscntFlag;
    }
}
