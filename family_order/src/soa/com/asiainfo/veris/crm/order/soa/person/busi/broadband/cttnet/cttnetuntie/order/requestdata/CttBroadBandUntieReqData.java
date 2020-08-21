
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetuntie.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class CttBroadBandUntieReqData extends BaseReqData
{
    private String phone; // 联系电话

    private String contact; // 联系人

    private String contactPhone; // 联系人电话

    private String standAddressCode; // 标准地址编码

    private String standAddress; // 标准地址

    private String connectType; // 接入类型

    private String addrDesc; // 安装地址

    private String mofficeId; // 局向

    private String modemStyle; // 锚方式 1：自备，2：租用 ，3：购买 ，4：赠送

    private String modemCode; // 猫编码

    private String modemNumberic; // 锚型号

    public String getAddrDesc()
    {
        return addrDesc;
    }

    public String getConnectType()
    {
        return connectType;
    }

    public String getContact()
    {
        return contact;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public String getModemCode()
    {
        return modemCode;
    }

    public String getModemNumberic()
    {
        return modemNumberic;
    }

    public String getModemStyle()
    {
        return modemStyle;
    }

    public String getMofficeId()
    {
        return mofficeId;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getStandAddress()
    {
        return standAddress;
    }

    public String getStandAddressCode()
    {
        return standAddressCode;
    }

    public void setAddrDesc(String addrDesc)
    {
        this.addrDesc = addrDesc;
    }

    public void setConnectType(String connectType)
    {
        this.connectType = connectType;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public void setModemCode(String modemCode)
    {
        this.modemCode = modemCode;
    }

    public void setModemNumberic(String modemNumberic)
    {
        this.modemNumberic = modemNumberic;
    }

    public void setModemStyle(String modemStyle)
    {
        this.modemStyle = modemStyle;
    }

    public void setMofficeId(String mofficeId)
    {
        this.mofficeId = mofficeId;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setStandAddress(String standAddress)
    {
        this.standAddress = standAddress;
    }

    public void setStandAddressCode(String standAddressCode)
    {
        this.standAddressCode = standAddressCode;
    }

}
