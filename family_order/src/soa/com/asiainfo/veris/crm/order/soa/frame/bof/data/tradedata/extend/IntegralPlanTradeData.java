
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class IntegralPlanTradeData extends BaseTradeData
{

    private String integralPlanInstId;

    private String integralAcctId;

    private String integralPlanId;

    private String custId;

    private String acctId;

    private String userId;

    private String startDate;

    private String endDate;

    private String status;

    private String modifyTag;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvStr10;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    public IntegralPlanTradeData()
    {

    }

    public IntegralPlanTradeData(IData data)
    {
        this.integralPlanInstId = data.getString("INTEGRAL_PLAN_INST_ID");
        this.integralAcctId = data.getString("INTEGRAL_ACCT_ID");
        this.integralPlanId = data.getString("INTEGRAL_PLAN_ID");
        this.custId = data.getString("CUST_ID");
        this.acctId = data.getString("ACCT_ID");
        this.userId = data.getString("USER_ID");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.status = data.getString("STATUS");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
    }

    public IntegralPlanTradeData clone()
    {
        IntegralPlanTradeData integralPlanTradeData = new IntegralPlanTradeData();
        integralPlanTradeData.setIntegralPlanInstId(this.getIntegralPlanInstId());
        integralPlanTradeData.setIntegralAcctId(this.getIntegralAcctId());
        integralPlanTradeData.setIntegralPlanId(this.getIntegralPlanId());
        integralPlanTradeData.setCustId(this.getCustId());
        integralPlanTradeData.setAcctId(this.getAcctId());
        integralPlanTradeData.setUserId(this.getUserId());
        integralPlanTradeData.setStartDate(this.getStartDate());
        integralPlanTradeData.setEndDate(this.getEndDate());
        integralPlanTradeData.setStatus(this.getStatus());
        integralPlanTradeData.setModifyTag(this.getModifyTag());
        integralPlanTradeData.setRemark(this.getRemark());
        integralPlanTradeData.setRsrvStr1(this.getRsrvStr1());
        integralPlanTradeData.setRsrvStr2(this.getRsrvStr2());
        integralPlanTradeData.setRsrvStr3(this.getRsrvStr3());
        integralPlanTradeData.setRsrvStr4(this.getRsrvStr4());
        integralPlanTradeData.setRsrvStr5(this.getRsrvStr5());
        integralPlanTradeData.setRsrvStr6(this.getRsrvStr6());
        integralPlanTradeData.setRsrvStr7(this.getRsrvStr7());
        integralPlanTradeData.setRsrvStr8(this.getRsrvStr8());
        integralPlanTradeData.setRsrvStr9(this.getRsrvStr9());
        integralPlanTradeData.setRsrvStr10(this.getRsrvStr10());
        integralPlanTradeData.setRsrvNum1(this.getRsrvNum1());
        integralPlanTradeData.setRsrvNum2(this.getRsrvNum2());
        integralPlanTradeData.setRsrvNum3(this.getRsrvNum3());
        integralPlanTradeData.setRsrvNum4(this.getRsrvNum4());
        integralPlanTradeData.setRsrvNum5(this.getRsrvNum5());
        integralPlanTradeData.setRsrvDate1(this.getRsrvDate1());
        integralPlanTradeData.setRsrvDate2(this.getRsrvDate2());
        integralPlanTradeData.setRsrvDate3(this.getRsrvDate3());
        integralPlanTradeData.setRsrvTag1(this.getRsrvTag1());
        integralPlanTradeData.setRsrvTag2(this.getRsrvTag2());
        integralPlanTradeData.setRsrvTag3(this.getRsrvTag3());

        return integralPlanTradeData;
    }

    public String getAcctId()
    {
        return this.acctId;
    }

    public String getCustId()
    {
        return this.custId;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getIntegralAcctId()
    {
        return this.integralAcctId;
    }

    public String getIntegralPlanId()
    {
        return this.integralPlanId;
    }

    public String getIntegralPlanInstId()
    {
        return this.integralPlanInstId;
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

    public String getRsrvNum1()
    {
        return this.rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return this.rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return this.rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return this.rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return this.rsrvNum5;
    }

    public String getRsrvStr1()
    {
        return this.rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return this.rsrvStr10;
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

    public String getRsrvStr6()
    {
        return this.rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return this.rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return this.rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return this.rsrvStr9;
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

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getStatus()
    {
        return this.status;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_INTEGRALPLAN";
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setIntegralAcctId(String integralAcctId)
    {
        this.integralAcctId = integralAcctId;
    }

    public void setIntegralPlanId(String integralPlanId)
    {
        this.integralPlanId = integralPlanId;
    }

    public void setIntegralPlanInstId(String integralPlanInstId)
    {
        this.integralPlanInstId = integralPlanInstId;
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

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("INTEGRAL_PLAN_INST_ID", this.integralPlanInstId);
        data.put("INTEGRAL_ACCT_ID", this.integralAcctId);
        data.put("INTEGRAL_PLAN_ID", this.integralPlanId);
        data.put("CUST_ID", this.custId);
        data.put("ACCT_ID", this.acctId);
        data.put("USER_ID", this.userId);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("STATUS", this.status);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
