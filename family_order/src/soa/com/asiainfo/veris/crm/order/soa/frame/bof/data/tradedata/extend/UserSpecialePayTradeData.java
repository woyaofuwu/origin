
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class UserSpecialePayTradeData extends BaseTradeData
{
    private String partitionId;

    private String userId;

    private String userIdA;

    private String acctId;

    private String acctIdB;

    private String payitemCode;

    private String startCycleId;

    private String endCycleId;

    private String bindType;

    private String limitType;

    private String limit;

    private String complementTag;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String updateStaffId;

    private String updateDepartId;

    private String updateTime;

    private String modifyTag;

    private String instId;

    public UserSpecialePayTradeData()
    {

    }

    public UserSpecialePayTradeData(IData data)
    {
        this.partitionId = data.getString("PARTITION_ID");
        this.userId = data.getString("USER_ID");
        this.userIdA = data.getString("USER_ID_A");
        this.acctId = data.getString("ACCT_ID");
        this.acctIdB = data.getString("ACCT_ID_B");
        this.payitemCode = data.getString("PAYITEM_CODE");
        this.startCycleId = data.getString("START_CYCLE_ID");
        this.endCycleId = data.getString("END_CYCLE_ID");
        this.bindType = data.getString("BIND_TYPE");
        this.limitType = data.getString("LIMIT_TYPE");
        this.limit = data.getString("LIMIT");
        this.complementTag = data.getString("COMPLEMENT_TAG");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.updateStaffId = data.getString("UPDATE_STAFF_ID");
        this.updateDepartId = data.getString("UPDATE_DEPART_ID");
        this.updateTime = data.getString("UPDATE_TIME");
        this.instId = data.getString("INST_ID");
    }

    public UserSpecialePayTradeData clone()
    {
        UserSpecialePayTradeData userSpecialePayTradeData = new UserSpecialePayTradeData();

        userSpecialePayTradeData.setPartitionId(this.getPartitionId());
        userSpecialePayTradeData.setUserId(this.getUserId());
        userSpecialePayTradeData.setUserIdA(this.getUserIdA());
        userSpecialePayTradeData.setAcctId(this.getAcctId());
        userSpecialePayTradeData.setAcctIdB(this.getAcctIdB());
        userSpecialePayTradeData.setPayitemCode(this.getPayitemCode());
        userSpecialePayTradeData.setStartCycleId(this.getStartCycleId());
        userSpecialePayTradeData.setEndCycleId(this.getEndCycleId());
        userSpecialePayTradeData.setBindType(this.getBindType());
        userSpecialePayTradeData.setLimitType(this.getLimitType());
        userSpecialePayTradeData.setLimit(this.getLimit());
        userSpecialePayTradeData.setComplementTag(this.getComplementTag());
        userSpecialePayTradeData.setRsrvStr1(this.getRsrvStr1());
        userSpecialePayTradeData.setRsrvStr2(this.getRsrvStr2());
        userSpecialePayTradeData.setRsrvStr3(this.getRsrvStr3());
        userSpecialePayTradeData.setUpdateStaffId(this.getUpdateStaffId());
        userSpecialePayTradeData.setUpdateDepartId(this.getUpdateDepartId());
        userSpecialePayTradeData.setUpdateTime(this.getUpdateTime());
        userSpecialePayTradeData.setModifyTag(this.getModifyTag());
        userSpecialePayTradeData.setInstId(this.getInstId());

        return userSpecialePayTradeData;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getAcctIdB()
    {
        return acctIdB;
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

    public String getPartitionId()
    {
        return partitionId;
    }

    public String getPayitemCode()
    {
        return payitemCode;
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

    public String getStartCycleId()
    {
        return startCycleId;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_USER_SPECIALEPAY";
    }

    public String getUpdateDepartId()
    {
        return updateDepartId;
    }

    public String getUpdateStaffId()
    {
        return updateStaffId;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAcctIdB(String acctIdB)
    {
        this.acctIdB = acctIdB;
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

    public void setPartitionId(String partitionId)
    {
        this.partitionId = partitionId;
    }

    public void setPayitemCode(String payitemCode)
    {
        this.payitemCode = payitemCode;
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

    public void setStartCycleId(String startCycleId)
    {
        this.startCycleId = startCycleId;
    }

    public void setUpdateDepartId(String updateDepartId)
    {
        this.updateDepartId = updateDepartId;
    }

    public void setUpdateStaffId(String updateStaffId)
    {
        this.updateStaffId = updateStaffId;
    }

    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
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
        data.put("PARTITION_ID", this.partitionId);
        data.put("USER_ID", this.userId);
        data.put("USER_ID_A", this.userIdA);
        data.put("ACCT_ID", this.acctId);
        data.put("ACCT_ID_B", this.acctIdB);
        data.put("PAYITEM_CODE", this.payitemCode);
        data.put("START_CYCLE_ID", this.startCycleId);
        data.put("END_CYCLE_ID", this.endCycleId);
        data.put("BIND_TYPE", this.bindType);
        data.put("LIMIT_TYPE", this.limitType);
        data.put("LIMIT", this.limit);
        data.put("COMPLEMENT_TAG", this.complementTag);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("UPDATE_STAFF_ID", this.updateStaffId);
        data.put("UPDATE_DEPART_ID", this.updateDepartId);
        data.put("UPDATE_TIME", this.updateTime);
        data.put("MODIFY_TAG", this.modifyTag);
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
