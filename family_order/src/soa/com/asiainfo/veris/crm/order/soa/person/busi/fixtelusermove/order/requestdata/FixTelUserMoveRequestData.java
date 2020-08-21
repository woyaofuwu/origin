
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class FixTelUserMoveRequestData extends BaseReqData
{
    private TelephoneChangeData telChangeData;// 移机信息

    private NumberChangeData numChangeData;// 换号信息

    private String changteleNotice;// 改号通知

    public FixTelUserMoveRequestData()
    {
        telChangeData = new TelephoneChangeData();
        numChangeData = new NumberChangeData();
    }

    public String getChangteleNotice()
    {
        return changteleNotice;
    }

    public NumberChangeData getNumChangeData()
    {
        return numChangeData;
    }

    public TelephoneChangeData getTelChangeData()
    {
        return telChangeData;
    }

    public void setChangteleNotice(String changteleNotice)
    {
        this.changteleNotice = changteleNotice;
    }

    public void setNumChangeData(NumberChangeData numChangeData)
    {
        this.numChangeData = numChangeData;
    }

    public void setTelChangeData(TelephoneChangeData telChangeData)
    {
        this.telChangeData = telChangeData;
    }
}
