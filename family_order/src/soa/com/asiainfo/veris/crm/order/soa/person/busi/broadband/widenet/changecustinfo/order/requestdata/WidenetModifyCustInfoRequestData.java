
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changecustinfo.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class WidenetModifyCustInfoRequestData extends BaseReqData
{
    private String newDetailAddress;

    private String newPsptId;

    private String newContactPhone;

    private String newContact;

    public String getNewContact()
    {
        return newContact;
    }

    public String getNewContactPhone()
    {
        return newContactPhone;
    }

    public String getNewDetailAddress()
    {
        return newDetailAddress;
    }

    public String getNewPsptId()
    {
        return newPsptId;
    }

    public void setNewContact(String newContact)
    {
        this.newContact = newContact;
    }

    public void setNewContactPhone(String newContactPhone)
    {
        this.newContactPhone = newContactPhone;
    }

    public void setNewDetailAddress(String newDetailAddress)
    {
        this.newDetailAddress = newDetailAddress;
    }

    public void setNewPsptId(String newPsptId)
    {
        this.newPsptId = newPsptId;
    }

}
