
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class PayRelationTradeData extends BaseTradeData
{
    private String acctId;

    private String acctPriority;

    private String actTag;

    private String addupMethod;

    private String addupMonths;

    private String bindType;

    private String complementTag;

    private String defaultTag;

    private String endCycleId;

    private String instId;

    private String limit;

    private String limitType;

    private String modifyTag;

    private String payitemCode;

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

    private String startCycleId;

    private String userId;

    private String userPriority;

    public PayRelationTradeData()
    {

    }

    public PayRelationTradeData(IData data)
    {
        this.setAcctId(data.getString("ACCT_ID"));
        this.setAcctPriority(data.getString("ACCT_PRIORITY"));
        this.setActTag(data.getString("ACT_TAG"));
        this.setAddupMethod(data.getString("ADDUP_METHOD"));
        this.setAddupMonths(data.getString("ADDUP_MONTHS"));
        this.setBindType(data.getString("BIND_TYPE"));
        this.setComplementTag(data.getString("COMPLEMENT_TAG"));
        this.setDefaultTag(data.getString("DEFAULT_TAG"));
        this.setEndCycleId(data.getString("END_CYCLE_ID"));
        this.setInstId(data.getString("INST_ID"));
        this.setLimit(data.getString("LIMIT"));
        this.setLimitType(data.getString("LIMIT_TYPE"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setPayitemCode(data.getString("PAYITEM_CODE"));
        this.setRemark(data.getString("REMARK"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvStr10(data.getString("RSRV_STR10"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvStr6(data.getString("RSRV_STR6"));
        this.setRsrvStr7(data.getString("RSRV_STR7"));
        this.setRsrvStr8(data.getString("RSRV_STR8"));
        this.setRsrvStr9(data.getString("RSRV_STR9"));
        this.setStartCycleId(data.getString("START_CYCLE_ID"));
        this.setUserId(data.getString("USER_ID"));
        this.setUserPriority(data.getString("USER_PRIORITY"));
    }

    public PayRelationTradeData clone()
    {
        PayRelationTradeData PayRelationTradeData = new PayRelationTradeData();
        PayRelationTradeData.setAcctId(this.getAcctId());
        PayRelationTradeData.setAcctPriority(this.getAcctPriority());
        PayRelationTradeData.setActTag(this.getActTag());
        PayRelationTradeData.setAddupMethod(this.getAddupMethod());
        PayRelationTradeData.setAddupMonths(this.getAddupMonths());
        PayRelationTradeData.setBindType(this.getBindType());
        PayRelationTradeData.setComplementTag(this.getComplementTag());
        PayRelationTradeData.setDefaultTag(this.getDefaultTag());
        PayRelationTradeData.setEndCycleId(this.getEndCycleId());
        PayRelationTradeData.setInstId(this.getInstId());
        PayRelationTradeData.setLimit(this.getLimit());
        PayRelationTradeData.setLimitType(this.getLimitType());
        PayRelationTradeData.setModifyTag(this.getModifyTag());
        PayRelationTradeData.setPayitemCode(this.getPayitemCode());
        PayRelationTradeData.setRemark(this.getRemark());
        PayRelationTradeData.setRsrvStr1(this.getRsrvStr1());
        PayRelationTradeData.setRsrvStr10(this.getRsrvStr10());
        PayRelationTradeData.setRsrvStr2(this.getRsrvStr2());
        PayRelationTradeData.setRsrvStr3(this.getRsrvStr3());
        PayRelationTradeData.setRsrvStr4(this.getRsrvStr4());
        PayRelationTradeData.setRsrvStr5(this.getRsrvStr5());
        PayRelationTradeData.setRsrvStr6(this.getRsrvStr6());
        PayRelationTradeData.setRsrvStr7(this.getRsrvStr7());
        PayRelationTradeData.setRsrvStr8(this.getRsrvStr8());
        PayRelationTradeData.setRsrvStr9(this.getRsrvStr9());
        PayRelationTradeData.setStartCycleId(this.getStartCycleId());
        PayRelationTradeData.setUserId(this.getUserId());
        PayRelationTradeData.setUserPriority(this.getUserPriority());
        return PayRelationTradeData;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getAcctPriority()
    {
        return acctPriority;
    }

    public String getActTag()
    {
        return actTag;
    }

    public String getAddupMethod()
    {
        return addupMethod;
    }

    public String getAddupMonths()
    {
        return addupMonths;
    }

    public String getBindType()
    {
        return bindType;
    }

    public String getComplementTag()
    {
        return complementTag;
    }

    public String getDefaultTag()
    {
        return defaultTag;
    }

    public String getEndCycleId()
    {
        return endCycleId;
    }

    public String getInstId()
    {
        return instId;
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

    public String getStartCycleId()
    {
        return startCycleId;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_PAYRELATION";
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserPriority()
    {
        return userPriority;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAcctPriority(String acctPriority)
    {
        this.acctPriority = acctPriority;
    }

    public void setActTag(String actTag)
    {
        this.actTag = actTag;
    }

    public void setAddupMethod(String addupMethod)
    {
        this.addupMethod = addupMethod;
    }

    public void setAddupMonths(String addupMonths)
    {
        this.addupMonths = addupMonths;
    }

    public void setBindType(String bindType)
    {
        this.bindType = bindType;
    }

    public void setComplementTag(String complementTag)
    {
        this.complementTag = complementTag;
    }

    public void setDefaultTag(String defaultTag)
    {
        this.defaultTag = defaultTag;
    }

    public void setEndCycleId(String endCycleId)
    {
        this.endCycleId = endCycleId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
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

    public void setStartCycleId(String startCycleId)
    {
        this.startCycleId = startCycleId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserPriority(String userPriority)
    {
        this.userPriority = userPriority;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACCT_ID", this.acctId);
        data.put("ACCT_PRIORITY", this.acctPriority);
        data.put("ACT_TAG", this.actTag);
        data.put("ADDUP_METHOD", this.addupMethod);
        data.put("ADDUP_MONTHS", this.addupMonths);
        data.put("BIND_TYPE", this.bindType);
        data.put("COMPLEMENT_TAG", this.complementTag);
        data.put("DEFAULT_TAG", this.defaultTag);
        data.put("END_CYCLE_ID", this.endCycleId);
        data.put("INST_ID", this.instId);
        data.put("LIMIT", this.limit);
        data.put("LIMIT_TYPE", this.limitType);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("PAYITEM_CODE", this.payitemCode);
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
        data.put("START_CYCLE_ID", this.startCycleId);
        data.put("USER_ID", this.userId);
        data.put("USER_PRIORITY", this.userPriority);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
