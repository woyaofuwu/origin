package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqGPRSCardLogId extends SeqBase
{
    public SeqGPRSCardLogId()
    {
        super("SEQ_GPRSC_LOG_ID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        return nextval;
    }

    @Override
    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
