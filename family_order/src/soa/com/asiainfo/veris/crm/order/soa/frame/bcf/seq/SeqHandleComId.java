package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqHandleComId extends SeqBase
{
    
    public SeqHandleComId()
    {
        super("SEQ_HANDLE_COMPLAINT_ID", 100);
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
