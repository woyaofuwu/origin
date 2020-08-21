
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

public final class AcctTimeEnv
{
    private String acctDay;

    private String firstDate;

    private String nextAcctDay;

    private String nextFirstDate;

    private String startDate;

    private String nextStartDate;

    public AcctTimeEnv(String acctDay, String firstDate, String startDate)
    {
        this.acctDay = acctDay;
        this.firstDate = firstDate;
        this.startDate = startDate;
    }

    public AcctTimeEnv(String acctDay, String firstDate, String nextAcctDay, String nextFirstDate)
    {
        this.acctDay = acctDay;
        this.firstDate = firstDate;
        this.nextAcctDay = nextAcctDay;
        this.nextFirstDate = nextFirstDate;
    }

    public AcctTimeEnv(String acctDay, String firstDate, String nextAcctDay, String nextFirstDate, String startDate, String nextStartDate)
    {
        this.acctDay = acctDay;
        this.firstDate = firstDate;
        this.nextAcctDay = nextAcctDay;
        this.nextFirstDate = nextFirstDate;
        this.startDate = startDate;
        this.nextStartDate = nextStartDate;
    }

    public String getAcctDay()
    {
        return acctDay;
    }

    public String getFirstDate()
    {
        return firstDate;
    }

    public String getNextAcctDay()
    {
        return nextAcctDay;
    }

    public String getNextFirstDate()
    {
        return nextFirstDate;
    }

    public String getNextStartDate()
    {
        return nextStartDate;
    }

    public String getStartDate()
    {
        return startDate;
    }
}
