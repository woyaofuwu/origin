
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqCopProjectId extends SeqBase
{
    public SeqCopProjectId()
    {
        super("SEQ_COP_PROJECT_ID", 100);
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
