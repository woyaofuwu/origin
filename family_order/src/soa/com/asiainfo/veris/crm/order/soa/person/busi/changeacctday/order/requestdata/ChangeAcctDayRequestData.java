
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctday.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ChangeAcctDayRequestData extends BaseReqData
{
    private String newAcctDay;// 新结账日

    public final String getNewAcctDay()
    {
        return newAcctDay;
    }

    public final void setNewAcctDay(String newAcctDay)
    {
        this.newAcctDay = newAcctDay;
    }
}
