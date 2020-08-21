
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class UserPayPlanTradeData extends BaseTradeData
{
    private String defaultTag;

    private String endDate;

    private String modifyTag;

    private String planDesc;

    private String planId;

    private String planName;

    private String planTypeCode;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr10;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String ruleId;

    private String startDate;

    private String userId;

    private String userIdA;

    public UserPayPlanTradeData()
    {

    }

    public UserPayPlanTradeData(IData data)
    {
        this.defaultTag = data.getString("DEFAULT_TAG");
        this.endDate = data.getString("END_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.planDesc = data.getString("PLAN_DESC");
        this.planId = data.getString("PLAN_ID");
        this.planName = data.getString("PLAN_NAME");
        this.planTypeCode = data.getString("PLAN_TYPE_CODE");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.ruleId = data.getString("RULE_ID");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.userIdA = data.getString("USER_ID_A");
    }

    public UserPayPlanTradeData clone()
    {
        UserPayPlanTradeData userPayPlanTradeData = new UserPayPlanTradeData();
        userPayPlanTradeData.setDefaultTag(this.getDefaultTag());
        userPayPlanTradeData.setEndDate(this.getEndDate());
        userPayPlanTradeData.setModifyTag(this.getModifyTag());
        userPayPlanTradeData.setPlanDesc(this.getPlanDesc());
        userPayPlanTradeData.setPlanId(this.getPlanId());
        userPayPlanTradeData.setPlanName(this.getPlanName());
        userPayPlanTradeData.setPlanTypeCode(this.getPlanTypeCode());
        userPayPlanTradeData.setRemark(this.getRemark());
        userPayPlanTradeData.setRsrvStr1(this.getRsrvStr1());
        userPayPlanTradeData.setRsrvStr10(this.getRsrvStr10());
        userPayPlanTradeData.setRsrvStr2(this.getRsrvStr2());
        userPayPlanTradeData.setRsrvStr3(this.getRsrvStr3());
        userPayPlanTradeData.setRsrvStr4(this.getRsrvStr4());
        userPayPlanTradeData.setRsrvStr5(this.getRsrvStr5());
        userPayPlanTradeData.setRsrvStr6(this.getRsrvStr6());
        userPayPlanTradeData.setRsrvStr7(this.getRsrvStr7());
        userPayPlanTradeData.setRsrvStr8(this.getRsrvStr8());
        userPayPlanTradeData.setRsrvStr9(this.getRsrvStr9());
        userPayPlanTradeData.setRuleId(this.getRuleId());
        userPayPlanTradeData.setStartDate(this.getStartDate());
        userPayPlanTradeData.setUserId(this.getUserId());
        userPayPlanTradeData.setUserIdA(this.getUserIdA());
        return userPayPlanTradeData;
    }

    public String getDefaultTag()
    {
        return defaultTag;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getPlanDesc()
    {
        return planDesc;
    }

    public String getPlanId()
    {
        return planId;
    }

    public String getPlanName()
    {
        return planName;
    }

    public String getPlanTypeCode()
    {
        return planTypeCode;
    }

    public String getRemark()
    {
        return remark;
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

    public String getRuleId()
    {
        return ruleId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_USER_PAYPLAN";
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public void setDefaultTag(String defaultTag)
    {
        this.defaultTag = defaultTag;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPlanDesc(String planDesc)
    {
        this.planDesc = planDesc;
    }

    public void setPlanId(String planId)
    {
        this.planId = planId;
    }

    public void setPlanName(String planName)
    {
        this.planName = planName;
    }

    public void setPlanTypeCode(String planTypeCode)
    {
        this.planTypeCode = planTypeCode;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("DEFAULT_TAG", this.defaultTag);
        data.put("END_DATE", this.endDate);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("PLAN_DESC", this.planDesc);
        data.put("PLAN_ID", this.planId);
        data.put("PLAN_NAME", this.planName);
        data.put("PLAN_TYPE_CODE", this.planTypeCode);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RULE_ID", this.ruleId);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("USER_ID_A", this.userIdA);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
