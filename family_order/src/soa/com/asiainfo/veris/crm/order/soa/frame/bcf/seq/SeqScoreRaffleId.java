package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqScoreRaffleId extends SeqBase
{
    
    public SeqScoreRaffleId()
    {
        super("SEQ_RAFFLE_LOG_ID", 100);
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