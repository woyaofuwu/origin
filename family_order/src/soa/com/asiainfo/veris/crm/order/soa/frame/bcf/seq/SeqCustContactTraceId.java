
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqCustContactTraceId extends SeqBase
{
    public SeqCustContactTraceId()
    {
        super("SEQ_CUST_CONTACT_TRACE", 100);
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

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
