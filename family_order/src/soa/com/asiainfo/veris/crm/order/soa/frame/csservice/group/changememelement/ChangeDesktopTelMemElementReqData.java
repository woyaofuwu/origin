
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

import com.ailk.common.data.IDataset;

public class ChangeDesktopTelMemElementReqData extends ChangeMemElementReqData
{
    private String cntrxMembPoer = "5";

    private String netTypeCode = "";

    private String power = "";

    private IDataset shorts;

    public String getCntrxMembPoer()
    {
        return cntrxMembPoer;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getPower()
    {
        return power;
    }

    public IDataset getShorts()
    {
        return shorts;
    }

    public void setCntrxMembPoer(String cntrxMembPoer)
    {
        this.cntrxMembPoer = cntrxMembPoer;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setPower(String power)
    {
        this.power = power;
    }

    public void setShorts(IDataset shorts)
    {
        this.shorts = shorts;
    }

}
