
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqCHL_SA_COMPACTID extends SeqBase
{
    public SeqCHL_SA_COMPACTID()
    {
        super("SEQ_CHL_SA_COMPACTID", 100);
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

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
