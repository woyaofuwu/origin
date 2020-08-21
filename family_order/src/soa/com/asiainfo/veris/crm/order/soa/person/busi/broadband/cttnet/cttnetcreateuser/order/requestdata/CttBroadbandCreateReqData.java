
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.requestdata;

import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreateUserRequestData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CttBroadbandCreateReqData.java
 * @Description: 铁通宽带开户bean
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-3-12 上午11:05:00 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-12 yxd v1.0.0 修改原因
 */
public class CttBroadbandCreateReqData extends CreateUserRequestData
{

    // 账户信息
    private String superBankCode; // 上级银行

    private String superBank; // 上级银行名称

    // 宽带信息
    private String cttPhone; // 固网号码

    private String standAddressCode; // 标准地址编码

    private String standAddress; // 标准地址

    private String connectType; // 接入类型

    private String addrDesc; // 安装地址

    private String mofficeId; // 局向

    private String rate; // 速率

    private String modemStyle; // 锚方式 1：自备，2：租用 ，3：购买 ，4：赠送

    private String modemCode; // 猫编码

    private String modemNumberic; // 锚型号

    private String modemNumbericDesc; // 锚型号描述

    private String broadBandAcctId; // 宽带帐号

    private String broadBandPwd; // 宽带密码

    private String productMode;

    private String bandCode;

    // 用户基本信息
    private String phone; // 联系电话

    private String psptTypeCode; // 证件类型

    private String psptId; // 证件号码

    private String custName; // 用户姓名

    private String psptAddr; // 证件地址

    private String contact; // 联系人

    private String contactPhone; // 联系人电话

    private String cityName; // 业务区

    private String postAddr; // 通信地址

    private String postCode; // 通信邮编

    private String widenetIpAddr; // WIDENET_IP_ADDRESS
    
    private String agentCustName;// 经办人名称

    private String agentPsptTypeCode;// 经办人证件类型

    private String agentPsptId;// 经办人证件号码

    private String agentPsptAddr;// 经办人证件地址

    public String getAddrDesc()
    {
        return addrDesc;
    }

    public String getBandCode()
    {
        return bandCode;
    }

    public String getBroadBandAcctId()
    {
        return broadBandAcctId;
    }

    public String getBroadBandPwd()
    {
        return broadBandPwd;
    }

    public String getCityName()
    {
        return cityName;
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

    public String getCttPhone()
    {
        return cttPhone;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getModemCode()
    {
        return modemCode;
    }

    public String getModemNumberic()
    {
        return modemNumberic;
    }

    public String getModemNumbericDesc()
    {
        return modemNumbericDesc;
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

    public String getPostAddr()
    {
        return postAddr;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getProductMode()
    {
        return productMode;
    }

    public String getPsptAddr()
    {
        return psptAddr;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public String getRate()
    {
        return rate;
    }

    public String getStandAddress()
    {
        return standAddress;
    }

    public String getStandAddressCode()
    {
        return standAddressCode;
    }

    public String getSuperBank()
    {
        return superBank;
    }

    public String getSuperBankCode()
    {
        return superBankCode;
    }

    public String getWidenetIpAddr()
    {
        return widenetIpAddr;
    }

    public void setAddrDesc(String addrDesc)
    {
        this.addrDesc = addrDesc;
    }

    public void setBandCode(String bandCode)
    {
        this.bandCode = bandCode;
    }

    public void setBroadBandAcctId(String broadBandAcctId)
    {
        this.broadBandAcctId = broadBandAcctId;
    }

    public void setBroadBandPwd(String broadBandPwd)
    {
        this.broadBandPwd = broadBandPwd;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
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

    public void setCttPhone(String cttPhone)
    {
        this.cttPhone = cttPhone;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setModemCode(String modemCode)
    {
        this.modemCode = modemCode;
    }

    public void setModemNumberic(String modemNumberic)
    {
        this.modemNumberic = modemNumberic;
    }

    public void setModemNumbericDesc(String modemNumbericDesc)
    {
        this.modemNumbericDesc = modemNumbericDesc;
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

    public void setPostAddr(String postAddr)
    {
        this.postAddr = postAddr;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setProductMode(String productMode)
    {
        this.productMode = productMode;
    }

    public void setPsptAddr(String psptAddr)
    {
        this.psptAddr = psptAddr;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

    public void setRate(String rate)
    {
        this.rate = rate;
    }

    public void setStandAddress(String standAddress)
    {
        this.standAddress = standAddress;
    }

    public void setStandAddressCode(String standAddressCode)
    {
        this.standAddressCode = standAddressCode;
    }

    public void setSuperBank(String superBank)
    {
        this.superBank = superBank;
    }

    public void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

    public void setWidenetIpAddr(String widenetIpAddr)
    {
        this.widenetIpAddr = widenetIpAddr;
    }

    public String getAgentCustName()
    {
        return agentCustName;
    }

    public String getAgentPsptAddr()
    {
        return agentPsptAddr;
    }

    public String getAgentPsptId()
    {
        return agentPsptId;
    }

    public String getAgentPsptTypeCode()
    {
        return agentPsptTypeCode;
    }
    
    public void setAgentCustName(String agentCustName)
    {
        this.agentCustName = agentCustName;
    }

    public void setAgentPsptAddr(String agentPsptAddr)
    {
        this.agentPsptAddr = agentPsptAddr;
    }

    public void setAgentPsptId(String agentPsptId)
    {
        this.agentPsptId = agentPsptId;
    }

    public void setAgentPsptTypeCode(String agentPsptTypeCode)
    {
        this.agentPsptTypeCode = agentPsptTypeCode;
    }
}
