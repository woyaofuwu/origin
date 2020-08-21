
package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ServiceOperReqData extends BaseReqData
{
    private String serviceId;// 服务编码

    private String operCode;// 操作类型 04:暂停，05恢复

    private String servType;// 服务类型 0:国内GPRS；1：国际GPRS

    private String sendFlag;// 如果servType为0，则该字段必传 0:,1:,2:

    private String gprsTotal;//如果帐务传了GPRS_TOTAL这个参数，这个参数的值为“120M”，则给用户下发如下的提醒短信：您好！您本月数据流量已达120M，将暂停数据流量功能，并在下个月自动恢复。中国移动

    public String getOperCode()
    {
        return operCode;
    }

    public String getSendFlag()
    {
        return sendFlag;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public String getServType()
    {
        return servType;
    }

    public String getGprsTotal()
    {
        return gprsTotal;
    }
    
    public void setGprsTotal(String gprsTotal)
    {
        this.gprsTotal = gprsTotal;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public void setSendFlag(String sendFlag)
    {
        this.sendFlag = sendFlag;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public void setServType(String servType)
    {
        this.servType = servType;
    }
}
