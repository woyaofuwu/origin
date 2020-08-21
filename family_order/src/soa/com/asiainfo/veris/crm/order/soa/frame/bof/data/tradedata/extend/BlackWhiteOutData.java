
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class BlackWhiteOutData extends BaseTradeData
{
    private String userId;
    
    private String serialNumber;
    
    private String groupId;
    
    private String custName; //集团客户名称

    private String servCode;
    
    private String bizCode;

    private String userTypeCode;

    private String operState;
    
    private String provinceCode; //集团客户归属省代码
    private String province;  //成员号码所属省份
    private String syncType;
    private String status;
    private String bkResult;
    private String statusCode;

    private String startDate;
    private String endDate;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String instId;

    public BlackWhiteOutData()
    {

    }

    public BlackWhiteOutData(IData data)
    {
        this.bizCode = data.getString("BIZ_CODE");
        this.endDate = data.getString("END_DATE");
        this.groupId = data.getString("GROUP_ID");
        this.operState = data.getString("OPER_STATE");
        
        this.custName = data.getString("CUST_NAME");
        this.provinceCode = data.getString("PROVINCE_CODE");
        this.province = data.getString("PROVINCE");
        this.syncType = data.getString("SYNC_TYPE");
        this.status = data.getString("STATUS");
        this.bkResult = data.getString("BK_RESULT");
        this.statusCode = data.getString("STATUS_CODE");
        
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.servCode = data.getString("SERV_CODE");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.userTypeCode = data.getString("USER_TYPE_CODE");
        this.instId = data.getString("INST_ID");
    }

    public BlackWhiteOutData clone()
    {
        BlackWhiteOutData BlackWhiteOutData = new BlackWhiteOutData();
        
        BlackWhiteOutData.setBizCode(this.getBizCode());
        BlackWhiteOutData.setEndDate(this.getEndDate());
        BlackWhiteOutData.setGroupId(this.getGroupId());
        BlackWhiteOutData.setOperState(this.getOperState());
        
        BlackWhiteOutData.setCustName(this.getCustName());
        BlackWhiteOutData.setProvinceCode(this.getProvinceCode());
        BlackWhiteOutData.setProvince(this.getProvince());
        BlackWhiteOutData.setSyncType(this.getSyncType());
        BlackWhiteOutData.setStatus(this.getStatus());
        BlackWhiteOutData.setBkResult(this.getBkResult());
        BlackWhiteOutData.setStatusCode(this.getStatusCode());
        
        BlackWhiteOutData.setRsrvDate1(this.getRsrvDate1());
        BlackWhiteOutData.setRsrvDate2(this.getRsrvDate2());
        BlackWhiteOutData.setRsrvDate3(this.getRsrvDate3());
        BlackWhiteOutData.setRsrvNum1(this.getRsrvNum1());
        BlackWhiteOutData.setRsrvNum2(this.getRsrvNum2());
        BlackWhiteOutData.setRsrvNum3(this.getRsrvNum3());
        BlackWhiteOutData.setRsrvStr1(this.getRsrvStr1());
        BlackWhiteOutData.setRsrvStr2(this.getRsrvStr2());
        BlackWhiteOutData.setRsrvStr3(this.getRsrvStr3());
        BlackWhiteOutData.setRsrvTag1(this.getRsrvTag1());
        BlackWhiteOutData.setRsrvTag2(this.getRsrvTag2());
        BlackWhiteOutData.setRsrvTag3(this.getRsrvTag3());
        BlackWhiteOutData.setSerialNumber(this.getSerialNumber());
        BlackWhiteOutData.setServCode(this.getServCode());
        BlackWhiteOutData.setStartDate(this.getStartDate());
        BlackWhiteOutData.setUserId(this.getUserId());
        BlackWhiteOutData.setUserTypeCode(this.getUserTypeCode());
        BlackWhiteOutData.setInstId(this.getInstId());

        return BlackWhiteOutData;
    }

    public String getBizCode()
    {
        return bizCode;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getOperState()
    {
        return operState;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
    }

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public String getCustName()
    {
        return custName;
    }
    public String getProvinceCode()
    {
        return provinceCode;
    }
    public String getProvince()
    {
        return province;
    }
    public String getSyncType()
    {
        return syncType;
    }
    public String getStatus()
    {
        return status;
    }
    public String getBkResult()
    {
        return bkResult;
    }
    public String getStatusCode()
    {
        return statusCode;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getServCode()
    {
        return servCode;
    }
    
    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_F_BLACKWHITE_OUT";
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserTypeCode()
    {
        return userTypeCode;
    }

    public void setBizCode(String bizCode)
    {
        this.bizCode = bizCode;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setOperState(String operState)
    {
        this.operState = operState;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public void setRsrvNum2(String rsrvNum2)
    {
        this.rsrvNum2 = rsrvNum2;
    }

    public void setRsrvNum3(String rsrvNum3)
    {
        this.rsrvNum3 = rsrvNum3;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }
    public void setProvinceCode(String provinceCode)
    {
        this.provinceCode = provinceCode;
    }
    public void setProvince(String province)
    {
        this.province = province;
    }
    public void setSyncType(String syncType)
    {
        this.syncType = syncType;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public void setBkResult(String bkResult)
    {
        this.bkResult = bkResult;
    }
    public void setStatusCode(String statusCode)
    {
        this.statusCode = statusCode;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setServCode(String servCode)
    {
        this.servCode = servCode;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

    public IData toData()
    {
        IData data = new DataMap();
        
        data.put("BIZ_CODE", this.bizCode);
        data.put("END_DATE", this.endDate);
        data.put("GROUP_ID", this.groupId);
        data.put("OPER_STATE", this.operState);
        
        data.put("CUST_NAME", this.custName);
        data.put("PROVINCE_CODE", this.provinceCode);
        data.put("PROVINCE", this.province);
        data.put("SYNC_TYPE", this.syncType);
        data.put("STATUS", this.status);
        data.put("BK_RESULT", this.bkResult);
        data.put("STATUS_CODE", this.statusCode);
        
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("SERV_CODE", this.servCode);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("USER_TYPE_CODE", this.userTypeCode);
        data.put("INST_ID", this.instId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
