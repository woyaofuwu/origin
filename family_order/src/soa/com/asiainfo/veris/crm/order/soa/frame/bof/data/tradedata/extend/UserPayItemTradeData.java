
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class UserPayItemTradeData extends BaseTradeData
{
    private String actTag;

    private String bindType;

    private String complementTag;

    private String endCycleId;

    private String limit;

    private String limitType;

    private String modifyTag;

    private String payitemCode;

    private String planId;

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

    private String startCycleId;

    private String userId;

    public UserPayItemTradeData()
    {

    }

    public UserPayItemTradeData(IData data)
    {
        this.actTag = data.getString("ACT_TAG");
        this.bindType = data.getString("BIND_TYPE");
        this.complementTag = data.getString("COMPLEMENT_TAG");
        this.endCycleId = data.getString("END_CYCLE_ID");
        this.limit = data.getString("LIMIT");
        this.limitType = data.getString("LIMIT_TYPE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.payitemCode = data.getString("PAYITEM_CODE");
        this.planId = data.getString("PLAN_ID");
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
        this.startCycleId = data.getString("START_CYCLE_ID");
        this.userId = data.getString("USER_ID");
    }

    public UserPayItemTradeData clone()
    {
        UserPayItemTradeData userPayItemTradeData = new UserPayItemTradeData();
        userPayItemTradeData.setActTag(this.getActTag());
        userPayItemTradeData.setBindType(this.getBindType());
        userPayItemTradeData.setComplementTag(this.getComplementTag());
        userPayItemTradeData.setEndCycleId(this.getEndCycleId());
        userPayItemTradeData.setLimit(this.getLimit());
        userPayItemTradeData.setLimitType(this.getLimitType());
        userPayItemTradeData.setModifyTag(this.getModifyTag());
        userPayItemTradeData.setPayitemCode(this.getPayitemCode());
        userPayItemTradeData.setPlanId(this.getPlanId());
        userPayItemTradeData.setRemark(this.getRemark());
        userPayItemTradeData.setRsrvStr1(this.getRsrvStr1());
        userPayItemTradeData.setRsrvStr10(this.getRsrvStr10());
        userPayItemTradeData.setRsrvStr2(this.getRsrvStr2());
        userPayItemTradeData.setRsrvStr3(this.getRsrvStr3());
        userPayItemTradeData.setRsrvStr4(this.getRsrvStr4());
        userPayItemTradeData.setRsrvStr5(this.getRsrvStr5());
        userPayItemTradeData.setRsrvStr6(this.getRsrvStr6());
        userPayItemTradeData.setRsrvStr7(this.getRsrvStr7());
        userPayItemTradeData.setRsrvStr8(this.getRsrvStr8());
        userPayItemTradeData.setRsrvStr9(this.getRsrvStr9());
        userPayItemTradeData.setRuleId(this.getRuleId());
        userPayItemTradeData.setStartCycleId(this.getStartCycleId());
        userPayItemTradeData.setUserId(this.getUserId());
        return userPayItemTradeData;
    }

    public String getActTag()
    {
        return actTag;
    }

    public String getBindType()
    {
        return bindType;
    }

    public String getComplementTag()
    {
        return complementTag;
    }

    public String getEndCycleId()
    {
        return endCycleId;
    }

    public String getLimit()
    {
        return limit;
    }

    public String getLimitType()
    {
        return limitType;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getPayitemCode()
    {
        return payitemCode;
    }

    public String getPlanId()
    {
        return planId;
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

    public String getStartCycleId()
    {
        return startCycleId;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_USER_PAYITEM";
    }

    public String getUserId()
    {
        return userId;
    }

    public void setActTag(String actTag)
    {
        this.actTag = actTag;
    }

    public void setBindType(String bindType)
    {
        this.bindType = bindType;
    }

    public void setComplementTag(String complementTag)
    {
        this.complementTag = complementTag;
    }

    public void setEndCycleId(String endCycleId)
    {
        this.endCycleId = endCycleId;
    }

    public void setLimit(String limit)
    {
        this.limit = limit;
    }

    public void setLimitType(String limitType)
    {
        this.limitType = limitType;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPayitemCode(String payitemCode)
    {
        this.payitemCode = payitemCode;
    }

    public void setPlanId(String planId)
    {
        this.planId = planId;
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

    public void setStartCycleId(String startCycleId)
    {
        this.startCycleId = startCycleId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACT_TAG", this.actTag);
        data.put("BIND_TYPE", this.bindType);
        data.put("COMPLEMENT_TAG", this.complementTag);
        data.put("END_CYCLE_ID", this.endCycleId);
        data.put("LIMIT", this.limit);
        data.put("LIMIT_TYPE", this.limitType);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("PAYITEM_CODE", this.payitemCode);
        data.put("PLAN_ID", this.planId);
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
        data.put("START_CYCLE_ID", this.startCycleId);
        data.put("USER_ID", this.userId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
