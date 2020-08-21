
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqCHL_CHECK_ID extends SeqBase
{
    public SeqCHL_CHECK_ID()
    {
        super("SEQ_CHL_CHECK_ID", 100);
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
