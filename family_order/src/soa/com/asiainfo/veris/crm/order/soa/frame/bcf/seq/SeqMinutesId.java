package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqMinutesId extends SeqBase
{
    
    public SeqMinutesId()
    {
        super("SEQ_MINUTES_ID", 100);
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
