
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideuseractive.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class NoPhoneWideUserActiveRequestData extends BaseReqData
{
    /**
     *无手机宽带开户tradeId,无手机宽带开户服开回单调用此接口时用到
     */
    private String createUserTradeId;
    
    /**
     * 宽带开户完工时间,无手机宽带开户服开回单调用此接口时用到
     */
    private String finishDate;
    
    /**
     * 设备ID,无手机宽带开户服开回单调用此接口时用到
     */
    private String deviceId;
    
    /**
     * 端口ID ,无手机宽带开户服开回单调用此接口时用到
     */
    private String portId;
    
    /**
     * 标准地址,无手机宽带开户服开回单调用此接口时用到
     */
    private String standAddress;
    
    /**
     * ,无手机宽带开户服开回单调用此接口时用到
     */
    private String constructionAddr;
    
    /**
     * ,无手机宽带开户服开回单调用此接口时用到
     */
    private String constPhone;
    
    /**
     * ,无手机宽带开户服开回单调用此接口时用到
     */
    private String constStaffId;
    
    /**
     * ,无手机宽带开户服开回单调用此接口时用到
     */
    private String rsrvTag3;
    
    public String getCreateUserTradeId()
    {
        return createUserTradeId;
    }

    public void setCreateUserTradeId(String createUserTradeId)
    {
        this.createUserTradeId = createUserTradeId;
    }

    public String getFinishDate()
    {
        return finishDate;
    }

    public void setFinishDate(String finishDate)
    {
        this.finishDate = finishDate;
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getPortId()
    {
        return portId;
    }

    public void setPortId(String portId)
    {
        this.portId = portId;
    }

    public String getStandAddress()
    {
        return standAddress;
    }

    public void setStandAddress(String standAddress)
    {
        this.standAddress = standAddress;
    }

    public String getConstructionAddr()
    {
        return constructionAddr;
    }

    public void setConstructionAddr(String constructionAddr)
    {
        this.constructionAddr = constructionAddr;
    }

    public String getConstPhone()
    {
        return constPhone;
    }

    public void setConstPhone(String constPhone)
    {
        this.constPhone = constPhone;
    }

    public String getConstStaffId()
    {
        return constStaffId;
    }

    public void setConstStaffId(String constStaffId)
    {
        this.constStaffId = constStaffId;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }
}
