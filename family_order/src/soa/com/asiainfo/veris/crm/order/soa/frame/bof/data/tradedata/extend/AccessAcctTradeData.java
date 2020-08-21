
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class AccessAcctTradeData extends BaseTradeData
{
    private String instId;

    private String userId;

    private String serialNumber;

    private String accessType;

    private String accessAcct;

    private String accessPwd;

    private String modifyTag;

    private String startDate;

    private String endDate;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String ipType;

    public AccessAcctTradeData()
    {
    }

    public AccessAcctTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.userId = data.getString("USER_ID");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.accessType = data.getString("ACCESS_TYPE");
        this.accessAcct = data.getString("ACCESS_ACCT");
        this.accessPwd = data.getString("ACCESS_PWD");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.ipType = data.getString("IP_TYPE");
    }

    public AccessAcctTradeData clone()
    {
        AccessAcctTradeData accessAcctTradeData = new AccessAcctTradeData();
        accessAcctTradeData.setInstId(this.getInstId());
        accessAcctTradeData.setUserId(this.getUserId());
        accessAcctTradeData.setSerialNumber(this.getSerialNumber());
        accessAcctTradeData.setAccessType(this.getAccessType());
        accessAcctTradeData.setAccessAcct(this.getAccessAcct());
        accessAcctTradeData.setAccessPwd(this.getAccessPwd());
        accessAcctTradeData.setModifyTag(this.getModifyTag());
        accessAcctTradeData.setStartDate(this.getStartDate());
        accessAcctTradeData.setEndDate(this.getEndDate());
        accessAcctTradeData.setRemark(this.getRemark());
        accessAcctTradeData.setRsrvStr1(this.getRsrvStr1());
        accessAcctTradeData.setRsrvStr2(this.getRsrvStr2());
        accessAcctTradeData.setRsrvStr3(this.getRsrvStr3());
        accessAcctTradeData.setRsrvStr4(this.getRsrvStr4());
        accessAcctTradeData.setRsrvStr5(this.getRsrvStr5());
        accessAcctTradeData.setRsrvTag1(this.getRsrvTag1());
        accessAcctTradeData.setRsrvTag2(this.getRsrvTag2());
        accessAcctTradeData.setRsrvTag3(this.getRsrvTag3());
        accessAcctTradeData.setRsrvDate1(this.getRsrvDate1());
        accessAcctTradeData.setRsrvDate2(this.getRsrvDate2());
        accessAcctTradeData.setRsrvDate3(this.getRsrvDate3());
        accessAcctTradeData.setIpType(this.getIpType());

        return accessAcctTradeData;
    }

    public String getAccessAcct()
    {
        return this.accessAcct;
    }

    public String getAccessPwd()
    {
        return this.accessPwd;
    }

    public String getAccessType()
    {
        return this.accessType;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getInstId()
    {
        return this.instId;
    }

    public String getIpType()
    {
        return this.ipType;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getRsrvDate1()
    {
        return this.rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return this.rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return this.rsrvDate3;
    }

    public String getRsrvStr1()
    {
        return this.rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return this.rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return this.rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return this.rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return this.rsrvStr5;
    }

    public String getRsrvTag1()
    {
        return this.rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return this.rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return this.rsrvTag3;
    }

    public String getSerialNumber()
    {
        return this.serialNumber;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_ACCESS_ACCT";
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setAccessAcct(String accessAcct)
    {
        this.accessAcct = accessAcct;
    }

    public void setAccessPwd(String accessPwd)
    {
        this.accessPwd = accessPwd;
    }

    public void setAccessType(String accessType)
    {
        this.accessType = accessType;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setIpType(String ipType)
    {
        this.ipType = ipType;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
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

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("INST_ID", this.instId);
        data.put("USER_ID", this.userId);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("ACCESS_TYPE", this.accessType);
        data.put("ACCESS_ACCT", this.accessAcct);
        data.put("ACCESS_PWD", this.accessPwd);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("IP_TYPE", this.ipType);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
