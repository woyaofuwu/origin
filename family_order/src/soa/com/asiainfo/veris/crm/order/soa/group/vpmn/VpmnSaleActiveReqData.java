
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class VpmnSaleActiveReqData extends MemberReqData
{
    private String tradeId;

    private String orderId;

    private String acceptMonth; // 受理月份

    private String custId;

    private String custName;

    private String orderCustId;

    private String orderCustName;

    private String productId;

    private String brandCode;

    private String psptTypeCode; // 证件类型

    private String psptId; // 证件号码

    private String groupId;

    private String userId;

    private String serialNumber;

    private String userIdA;

    private String serialNumberA;

    private String userIdB;

    private String custIdB;

    private String serialNumberB;

    private String rsrvStr4; // 记录活动类型字段

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String printTemplets;

    private String discntType;

    // private String remark;
    private String tradeTypeCode;

    private String priority;// 优先级

    private String eparchyCode;

    private String tradeEparchCode;

    private String cityCode;

    private String tradeCityCode;

    private String updateStaffId;

    private String updateDeptId;

    private String sysDate;

    private String endDate;

    private String processTagSet;

    private String execTime;

    private String acctId;

    private String netTypeCode;

    private IDataset discnts;

    public String getAcceptMonth()
    {
        return acceptMonth;
    }

    public String getBrandCode()
    {
        return brandCode;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getCustIdB()
    {
        return custIdB;
    }

    public String getCustName()
    {
        return custName;
    }

    public IDataset getDiscnts()
    {
        return discnts;
    }

    public String getDiscntType()
    {
        return discntType;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getExecTime()
    {
        return execTime;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getOrderCustId()
    {
        return orderCustId;
    }

    public String getOrderCustName()
    {
        return orderCustName;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getPrintTemplets()
    {
        return printTemplets;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getProcessTagSet()
    {
        return processTagSet;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSerialNumberA()
    {
        return serialNumberA;
    }

    public String getSerialNumberB()
    {
        return serialNumberB;
    }

    public String getSysDate()
    {
        return sysDate;
    }

    public String getTradeCityCode()
    {
        return tradeCityCode;
    }

    public String getTradeEparchCode()
    {
        return tradeEparchCode;
    }

    public String getTradeId()
    {
        return tradeId;
    }

    // public String getRemark()
    // {
    // return remark;
    // }
    // public void setRemark(String remark)
    // {
    // this.remark = remark;
    // }
    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public String getUpdateDeptId()
    {
        return updateDeptId;
    }

    public String getUpdateStaffId()
    {
        return updateStaffId;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public String getUserIdB()
    {
        return userIdB;
    }

    public void setAcceptMonth(String acceptMonth)
    {
        this.acceptMonth = acceptMonth;
    }

    public void setBrandCode(String brandCode)
    {
        this.brandCode = brandCode;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setCustIdB(String custIdB)
    {
        this.custIdB = custIdB;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setDiscnts(IDataset discnts)
    {
        this.discnts = discnts;
    }

    public void setDiscntType(String discntType)
    {
        this.discntType = discntType;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setExecTime(String execTime)
    {
        this.execTime = execTime;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public void setOrderCustId(String orderCustId)
    {
        this.orderCustId = orderCustId;
    }

    public void setOrderCustName(String orderCustName)
    {
        this.orderCustName = orderCustName;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setPrintTemplets(String printTemplets)
    {
        this.printTemplets = printTemplets;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public void setProcessTagSet(String processTagSet)
    {
        this.processTagSet = processTagSet;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSerialNumberA(String serialNumberA)
    {
        this.serialNumberA = serialNumberA;
    }

    public void setSerialNumberB(String serialNumberB)
    {
        this.serialNumberB = serialNumberB;
    }

    public void setSysDate(String sysDate)
    {
        this.sysDate = sysDate;
    }

    public void setTradeCityCode(String tradeCityCode)
    {
        this.tradeCityCode = tradeCityCode;
    }

    public void setTradeEparchCode(String tradeEparchCode)
    {
        this.tradeEparchCode = tradeEparchCode;
    }

    public void setTradeId(String tradeId)
    {
        this.tradeId = tradeId;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setUpdateDeptId(String updateDeptId)
    {
        this.updateDeptId = updateDeptId;
    }

    public void setUpdateStaffId(String updateStaffId)
    {
        this.updateStaffId = updateStaffId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    public void setUserIdB(String userIdB)
    {
        this.userIdB = userIdB;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

}
