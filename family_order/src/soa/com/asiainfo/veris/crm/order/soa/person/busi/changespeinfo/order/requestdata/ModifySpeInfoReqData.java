
package com.asiainfo.veris.crm.order.soa.person.busi.changespeinfo.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ModifySpeInfoReqData extends BaseReqData
{

    private String brandCode;// 用户品牌

    private String userTypeCode;// 用户类型

    private String simCardNo;// 用户sim卡号

    private String imsi;// 用户IMSI

    private String cityCode;// 用户业务区

    private String userStateCode;// 用户状态

    private String developStaffId;// 发展工号

    public String getBrandCode()
    {
        return brandCode;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getDevelopStaffId()
    {
        return developStaffId;
    }

    public String getImsi()
    {
        return imsi;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public String getUserStateCode()
    {
        return userStateCode;
    }

    public String getUserTypeCode()
    {
        return userTypeCode;
    }

    public void setBrandCode(String brandCode)
    {
        this.brandCode = brandCode;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setDevelopStaffId(String developStaffId)
    {
        this.developStaffId = developStaffId;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
    }

    public void setUserStateCode(String userStateCode)
    {
        this.userStateCode = userStateCode;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

}
