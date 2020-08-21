
package com.asiainfo.veris.crm.order.soa.person.busi.score.getgiftofstudyimbursement.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseGiftOfStudyRequestData extends BaseReqData
{

    private String serialNumber = "";

    private String giftsInfo = null;

    public String getGiftsInfo()
    {
        return giftsInfo;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setGiftsInfo(String giftsInfo)
    {
        this.giftsInfo = giftsInfo;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

}
