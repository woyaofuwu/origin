
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqCmpayLogId extends SeqBase
{

    public SeqCmpayLogId()
    {
        super("SEQ_CMPAY_LOG_ID", 100);
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
        return getNextval(s);
    }

}
