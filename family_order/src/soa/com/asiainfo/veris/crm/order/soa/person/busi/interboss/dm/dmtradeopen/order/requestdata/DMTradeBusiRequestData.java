
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradeopen.order.requestdata;

public class DMTradeBusiRequestData extends BaseDMTradeRequestData
{

    private String serialNum;

    private String resCode;

    private String updateTag;

    private String DMTag;

    public String getDMTag()
    {
        return DMTag;
    }

    public String getResCode()
    {
        return resCode;
    }

    public String getSerialNum()
    {
        return serialNum;
    }

    public String getUpdateTag()
    {
        return updateTag;
    }

    public void setDMTag(String tag)
    {
        DMTag = tag;
    }

    public void setResCode(String resCode)
    {
        this.resCode = resCode;
    }

    public void setSerialNum(String serialNum)
    {
        this.serialNum = serialNum;
    }

    public void setUpdateTag(String updateTag)
    {
        this.updateTag = updateTag;
    }
}
