
package com.asiainfo.veris.crm.order.soa.person.busi.serviceMaintain.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

import java.util.Date;

public class ServiceMaintainReqData extends BaseReqData
{
    // 服务号码
   // private String serialNumber;

    // 状态   0：未完成   1: 已完成
    private String state;

    // 入表时间
//    private Date in_date;

    // 操作类型;   0: 开通  1： 退订
    private String operateType;

    // 基础服务类型名称
//    private String baseService;

    // 基础服务类型ID
    private String baseServiceID;

//    public String getSerialNumber() {
//        return serialNumber;
//    }
//
//    public void setSerialNumber(String serialNumber) {
//        this.serialNumber = serialNumber;
//    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

//    public Date getIn_date() {
//        return in_date;
//    }
//
//    public void setIn_date(Date in_date) {
//        this.in_date = in_date;
//    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

//    public String getBaseService() {
//        return baseService;
//    }
//
//    public void setBaseService(String baseService) {
//        this.baseService = baseService;
//    }

    public String getBaseServiceID() {
        return baseServiceID;
    }

    public void setBaseServiceID(String baseServiceID) {
        this.baseServiceID = baseServiceID;
    }
}
