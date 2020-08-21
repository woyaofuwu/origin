
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.resetaccount.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ResetAccountRequestData extends BaseReqData
{
    private String serialNumber;// 宽带账号

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }
}
