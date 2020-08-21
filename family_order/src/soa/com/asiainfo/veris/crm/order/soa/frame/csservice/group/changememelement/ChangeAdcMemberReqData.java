
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

import com.ailk.common.data.IData;

public class ChangeAdcMemberReqData extends ChangeMemElementReqData
{
    private String synDiscntFlag;// 是否同步优惠

    private IData oldxxtName;

    private IData oldxxtPhone;

    private IData newxxtName;

    private IData newxxtPhone;

    public IData getNewxxtName()
    {
        return newxxtName;
    }

    public IData getNewxxtPhone()
    {
        return newxxtPhone;
    }

    public IData getOldxxtName()
    {
        return oldxxtName;
    }

    public IData getOldxxtPhone()
    {
        return oldxxtPhone;
    }

    public String getSynDiscntFlag()
    {
        return synDiscntFlag;
    }

    public void setNewxxtName(IData newxxtName)
    {
        this.newxxtName = newxxtName;
    }

    public void setNewxxtPhone(IData newxxtPhone)
    {
        this.newxxtPhone = newxxtPhone;
    }

    public void setOldxxtName(IData oldxxtName)
    {
        this.oldxxtName = oldxxtName;
    }

    public void setOldxxtPhone(IData oldxxtPhone)
    {
        this.oldxxtPhone = oldxxtPhone;
    }

    public void setSynDiscntFlag(String synDiscntFlag)
    {
        this.synDiscntFlag = synDiscntFlag;
    }
}
