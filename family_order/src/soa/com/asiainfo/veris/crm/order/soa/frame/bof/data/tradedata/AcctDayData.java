
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

public class AcctDayData
{
    private String id;

    private String acctDay;

    private boolean needSyncSameAccountUser = true;

    public AcctDayData(String id, String acctDay)
    {
        this.id = id;
        this.acctDay = acctDay;
    }

    public AcctDayData(String id, String acctDay, boolean needSyncSameAccountUser)
    {
        this.id = id;
        this.acctDay = acctDay;
        this.needSyncSameAccountUser = needSyncSameAccountUser;
    }

    public String getAcctDay()
    {
        return acctDay;
    }

    public String getId()
    {
        return id;
    }

    public boolean isNeedSyncSameAccountUser()
    {
        return needSyncSameAccountUser;
    }

    public void setAcctDay(String acctDay)
    {
        this.acctDay = acctDay;
    }

    public void setId(String userId)
    {
        this.id = userId;
    }

    public void setNeedSyncSameAccountUser(boolean needSyncSameAccountUser)
    {
        this.needSyncSameAccountUser = needSyncSameAccountUser;
    }

}
