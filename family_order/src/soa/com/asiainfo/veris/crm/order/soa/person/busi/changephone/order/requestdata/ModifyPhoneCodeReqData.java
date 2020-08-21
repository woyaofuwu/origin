
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardBaseReqData;

public class ModifyPhoneCodeReqData extends BaseReqData
{
    private String newSerialNumber;

    private String inherit;
    
    private SimCardBaseReqData newSimCardInfo;

    private SimCardBaseReqData oldSimCardInfo;

    public String getNewSerialNumber()
    {
        return newSerialNumber;
    }

    public SimCardBaseReqData getNewSimCardInfo()
    {
        return newSimCardInfo;
    }
    
    public String getInherit()
    {
        return inherit;
    }

    public SimCardBaseReqData getOldSimCardInfo()
    {
        return oldSimCardInfo;
    }

    public void setNewSerialNumber(String newSerialNumber)
    {
        this.newSerialNumber = newSerialNumber;
    }
    
    
    public void setInherit(String inherit)
    {
        this.inherit = inherit;
    }

    public void setNewSimCardInfo(SimCardBaseReqData newSimCardInfo)
    {
        this.newSimCardInfo = newSimCardInfo;
    }

    public void setOldSimCardInfo(SimCardBaseReqData oldSimCardInfo)
    {
        this.oldSimCardInfo = oldSimCardInfo;
    }

}
