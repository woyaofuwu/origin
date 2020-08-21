
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqUserIdentCode extends SeqBase
{

    public SeqUserIdentCode()
    {
        super("SEQ_USER_IDENT_CODE", 100);
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
