
package com.asiainfo.veris.crm.order.soa.person.busi.rentmobile.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class RentMobileReqData extends BaseReqData
{
    public String rentSerialNumber;// 租机号码

    public String rentTag;// 租机状态--0：退机；1：租机

    public String rentModeCode;// 租机方式

    public String rentTypeCode;// 租机类型

    public String invoiceNo;// 发票号码

    public String simCardNo;// SIM卡号

    public String imsi;

    public String startDate;

    public String money;

    public String feeSerialNumber;// 租机计费号码

    public String areacode;// 国际区号

    public String rentImei;// 租机IMEI

    public String getAreacode()
    {
        return areacode;
    }

    public String getFeeSerialNumber()
    {
        return feeSerialNumber;
    }

    public String getImsi()
    {
        return imsi;
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public String getMoney()
    {
        return money;
    }

    public String getRentImei()
    {
        return rentImei;
    }

    public String getRentModeCode()
    {
        return rentModeCode;
    }

    public String getRentSerialNumber()
    {
        return rentSerialNumber;
    }

    public String getRentTag()
    {
        return rentTag;
    }

    public String getRentTypeCode()
    {
        return rentTypeCode;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setAreacode(String areacode)
    {
        this.areacode = areacode;
    }

    public void setFeeSerialNumber(String feeSerialNumber)
    {
        this.feeSerialNumber = feeSerialNumber;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public void setMoney(String money)
    {
        this.money = money;
    }

    public void setRentImei(String rentImei)
    {
        this.rentImei = rentImei;
    }

    public void setRentModeCode(String rentModeCode)
    {
        this.rentModeCode = rentModeCode;
    }

    public void setRentSerialNumber(String rentSerialNumber)
    {
        this.rentSerialNumber = rentSerialNumber;
    }

    public void setRentTag(String rentTag)
    {
        this.rentTag = rentTag;
    }

    public void setRentTypeCode(String rentTypeCode)
    {
        this.rentTypeCode = rentTypeCode;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }
}
