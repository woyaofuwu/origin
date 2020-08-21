
package com.asiainfo.veris.crm.iorder.soa.family.common.data;

import com.ailk.common.data.IData;

/**
 * @Description 家庭客户Bean
 * @Auther: zhenggang
 * @Date: 2020/7/30 11:09
 * @version: V1.0
 */
public class CustFamilyData
{
    // 家庭昵称
    private String homeName;

    // 家庭住址
    private String homeAddress;

    // 家庭联系电话
    private String homePhone;

    // 归属地区
    private String honeRegion;

    // 户主名称
    private String custName;

    // 户主号码
    private String headSerialNumber;

    // 户主证件类型
    private String headPsptTypeCode;

    // 户主证件ID
    private String headPsptId;

    public CustFamilyData(IData data)
    {
        this.setHomeName(data.getString("HOME_NAME"));
        this.setHomeAddress(data.getString("HOME_ADDRESS"));
        this.setHomePhone(data.getString("HOME_PHONE"));
        this.setHoneRegion(data.getString("HOME_REGION"));
        this.setCustName(data.getString("CUST_NAME"));
        this.setHeadSerialNumber(data.getString("HEAD_SERIAL_NUMBER"));
        this.setHeadPsptTypeCode(data.getString("HEAD_PSPT_TYPE_CODE"));
        this.setHeadPsptId(data.getString("HEAD_PSPT_ID"));
    }

    public String getHomeName()
    {
        return homeName;
    }

    public void setHomeName(String homeName)
    {
        this.homeName = homeName;
    }

    public String getHomeAddress()
    {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress)
    {
        this.homeAddress = homeAddress;
    }

    public String getHomePhone()
    {
        return homePhone;
    }

    public void setHomePhone(String homePhone)
    {
        this.homePhone = homePhone;
    }

    public String getHoneRegion()
    {
        return honeRegion;
    }

    public void setHoneRegion(String honeRegion)
    {
        this.honeRegion = honeRegion;
    }

    public String getCustName()
    {
        return custName;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public String getHeadSerialNumber()
    {
        return headSerialNumber;
    }

    public void setHeadSerialNumber(String headSerialNumber)
    {
        this.headSerialNumber = headSerialNumber;
    }

    public String getHeadPsptTypeCode()
    {
        return headPsptTypeCode;
    }

    public void setHeadPsptTypeCode(String headPsptTypeCode)
    {
        this.headPsptTypeCode = headPsptTypeCode;
    }

    public String getHeadPsptId()
    {
        return headPsptId;
    }

    public void setHeadPsptId(String headPsptId)
    {
        this.headPsptId = headPsptId;
    }
}
