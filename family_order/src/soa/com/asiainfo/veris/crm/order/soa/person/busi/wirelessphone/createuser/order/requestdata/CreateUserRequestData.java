/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.BaseCreateUserRequestData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreateUserRequestData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-9 上午09:58:18 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
 */

public class CreateUserRequestData extends BaseCreateUserRequestData
{

    private String superBankCode; // 上级银行

    private PostInfoData postInfo;

    private String serialNumber;

    private String serialNumberBind;

    private String simCardNo;

    private String imsi;
    
    private String openType;

    private String giftTelphone;
    
    private String areaType;
    
    private List<DeviceInfoData> deviceList;

    public List<DeviceInfoData> getDeviceList()
    {
        return deviceList;
    }

    public String getImsi()
    {
        return imsi;
    }

    public PostInfoData getPostInfo()
    {
        return postInfo;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSerialNumberBind()
    {
        return serialNumberBind;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public String getSuperBankCode()
    {
        return superBankCode;
    }

    public void setDeviceList(List<DeviceInfoData> deviceList)
    {
        this.deviceList = deviceList;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public void setPostInfo(PostInfoData postInfo)
    {
        this.postInfo = postInfo;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSerialNumberBind(String serialNumberBind)
    {
        this.serialNumberBind = serialNumberBind;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
    }

    public void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

	public String getGiftTelphone() {
		return giftTelphone;
	}

	public void setGiftTelphone(String giftTelphone) {
		this.giftTelphone = giftTelphone;
	}

	public String getAreaType() {
		return areaType;
	}

	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}

    
}
