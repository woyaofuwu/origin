
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class IntegralAcctTradeData extends BaseTradeData
{

    private String rsrvStr2;

    private String rsrvStr1;

    private String rsrvStr4;

    private String modifyTag;

    private String rsrvStr3;

    private String rsrvStr6;

    private String rsrvStr5;

    private String endDate;

    private String rsrvStr8;

    private String rsrvStr7;

    private String remark;

    private String rsrvStr9;

    private String status;

    private String integralAccountType;

    private String psptId;

    private String rsrvTag3;

    private String rsrvTag2;

    private String custId;

    private String acctId;

    private String rsrvTag1;

    private String email;

    private String password;

    private String name;

    private String userId;

    private String address;

    private String useLimit;

    private String rsrvStr10;

    private String rowid;

    private String rsrvNum5;

    private String rsrvNum4;

    private String startDate;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvDate1;

    private String contractPhone;

    private String integralAcctId;

    private String rsrvNum1;

    private String rsrvNum3;

    private String rsrvNum2;

    private String psptTypeCode;

    public IntegralAcctTradeData()
    {

    }

    public IntegralAcctTradeData(IData data)
    {
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.endDate = data.getString("END_DATE");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.remark = data.getString("REMARK");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.status = data.getString("STATUS");
        this.integralAccountType = data.getString("INTEGRAL_ACCOUNT_TYPE");
        this.psptId = data.getString("PSPT_ID");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.custId = data.getString("CUST_ID");
        this.acctId = data.getString("ACCT_ID");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.email = data.getString("EMAIL");
        this.password = data.getString("PASSWORD");
        this.name = data.getString("NAME");
        this.userId = data.getString("USER_ID");
        this.address = data.getString("ADDRESS");
        this.useLimit = data.getString("USE_LIMIT");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rowid = data.getString("ROWID");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.startDate = data.getString("START_DATE");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.contractPhone = data.getString("CONTRACT_PHONE");
        this.integralAcctId = data.getString("INTEGRAL_ACCT_ID");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.psptTypeCode = data.getString("PSPT_TYPE_CODE");
    }

    public IntegralAcctTradeData clone()
    {
        IntegralAcctTradeData integralAcctTradeData = new IntegralAcctTradeData();

        integralAcctTradeData.setRsrvStr2(this.getRsrvStr2());
        integralAcctTradeData.setRsrvStr1(this.getRsrvStr1());
        integralAcctTradeData.setRsrvStr4(this.getRsrvStr4());
        integralAcctTradeData.setModifyTag(this.getModifyTag());
        integralAcctTradeData.setRsrvStr3(this.getRsrvStr3());
        integralAcctTradeData.setRsrvStr6(this.getRsrvStr6());
        integralAcctTradeData.setRsrvStr5(this.getRsrvStr5());
        integralAcctTradeData.setEndDate(this.getEndDate());
        integralAcctTradeData.setRsrvStr8(this.getRsrvStr8());
        integralAcctTradeData.setRsrvStr7(this.getRsrvStr7());
        integralAcctTradeData.setRemark(this.getRemark());
        integralAcctTradeData.setRsrvStr9(this.getRsrvStr9());
        integralAcctTradeData.setStatus(this.getStatus());
        integralAcctTradeData.setIntegralAccountType(this.getIntegralAccountType());
        integralAcctTradeData.setPsptId(this.getPsptId());
        integralAcctTradeData.setRsrvTag3(this.getRsrvTag3());
        integralAcctTradeData.setRsrvTag2(this.getRsrvTag2());
        integralAcctTradeData.setCustId(this.getCustId());
        integralAcctTradeData.setAcctId(this.getAcctId());
        integralAcctTradeData.setRsrvTag1(this.getRsrvTag1());
        integralAcctTradeData.setEmail(this.getEmail());
        integralAcctTradeData.setPassword(this.getPassword());
        integralAcctTradeData.setName(this.getName());
        integralAcctTradeData.setUserId(this.getUserId());
        integralAcctTradeData.setAddress(this.getAddress());
        integralAcctTradeData.setUseLimit(this.getUseLimit());
        integralAcctTradeData.setRsrvStr10(this.getRsrvStr10());
        integralAcctTradeData.setRowid(this.getRowid());
        integralAcctTradeData.setRsrvNum5(this.getRsrvNum5());
        integralAcctTradeData.setRsrvNum4(this.getRsrvNum4());
        integralAcctTradeData.setStartDate(this.getStartDate());
        integralAcctTradeData.setRsrvDate2(this.getRsrvDate2());
        integralAcctTradeData.setRsrvDate3(this.getRsrvDate3());
        integralAcctTradeData.setRsrvDate1(this.getRsrvDate1());
        integralAcctTradeData.setContractPhone(this.getContractPhone());
        integralAcctTradeData.setIntegralAcctId(this.getIntegralAcctId());
        integralAcctTradeData.setRsrvNum1(this.getRsrvNum1());
        integralAcctTradeData.setRsrvNum3(this.getRsrvNum3());
        integralAcctTradeData.setRsrvNum2(this.getRsrvNum2());
        integralAcctTradeData.setPsptTypeCode(this.getPsptTypeCode());
        return integralAcctTradeData;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getAddress()
    {
        return address;
    }

    public String getContractPhone()
    {
        return contractPhone;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getEmail()
    {
        return email;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getIntegralAccountType()
    {
        return integralAccountType;
    }

    public String getIntegralAcctId()
    {
        return integralAcctId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getName()
    {
        return name;
    }

    public String getPassword()
    {
        return password;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRowid()
    {
        return rowid;
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

    public String getRsrvNum4()
    {
        return rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return rsrvNum5;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
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

    public String getRsrvStr8()
    {
        return rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return rsrvStr9;
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

    public String getStartDate()
    {
        return startDate;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_INTEGRALACCT";
    }

    public String getUseLimit()
    {
        return useLimit;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setContractPhone(String contractPhone)
    {
        this.contractPhone = contractPhone;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setIntegralAccountType(String integralAccountType)
    {
        this.integralAccountType = integralAccountType;
    }

    public void setIntegralAcctId(String integralAcctId)
    {
        this.integralAcctId = integralAcctId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRowid(String rowid)
    {
        this.rowid = rowid;
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

    public void setRsrvNum4(String rsrvNum4)
    {
        this.rsrvNum4 = rsrvNum4;
    }

    public void setRsrvNum5(String rsrvNum5)
    {
        this.rsrvNum5 = rsrvNum5;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
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

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setRsrvStr8(String rsrvStr8)
    {
        this.rsrvStr8 = rsrvStr8;
    }

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
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

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setUseLimit(String useLimit)
    {
        this.useLimit = useLimit;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("END_DATE", this.endDate);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("STATUS", this.status);
        data.put("INTEGRAL_ACCOUNT_TYPE", this.integralAccountType);
        data.put("PSPT_ID", this.psptId);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("CUST_ID", this.custId);
        data.put("ACCT_ID", this.acctId);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("EMAIL", this.email);
        data.put("PASSWORD", this.password);
        data.put("NAME", this.name);
        data.put("USER_ID", this.userId);
        data.put("ADDRESS", this.address);
        data.put("USE_LIMIT", this.useLimit);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("ROWID", this.rowid);
        data.put("RSRV_NUM5", this.rsrvNum5);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("START_DATE", this.startDate);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("CONTRACT_PHONE", this.contractPhone);
        data.put("INTEGRAL_ACCT_ID", this.integralAcctId);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("PSPT_TYPE_CODE", this.psptTypeCode);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
