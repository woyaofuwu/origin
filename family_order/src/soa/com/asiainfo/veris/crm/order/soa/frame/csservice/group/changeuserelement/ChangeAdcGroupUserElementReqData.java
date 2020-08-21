
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

public class ChangeAdcGroupUserElementReqData extends ChangeUserElementReqData
{
    private String synDiscntFlag;// 是否同步优惠

    private String hireFee;// 设备租赁费

    private String feeCycle;// 计费周期

    private String contractId;

    public String getContractId()
    {
        return contractId;
    }

    public String getFeeCycle()
    {
        return feeCycle;
    }

    public String getHireFee()
    {
        return hireFee;
    }

    public String getSynDiscntFlag()
    {
        return synDiscntFlag;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public void setFeeCycle(String feeCycle)
    {
        this.feeCycle = feeCycle;
    }

    public void setHireFee(String hireFee)
    {
        this.hireFee = hireFee;
    }

    public void setSynDiscntFlag(String synDiscntFlag)
    {
        this.synDiscntFlag = synDiscntFlag;
    }
}
