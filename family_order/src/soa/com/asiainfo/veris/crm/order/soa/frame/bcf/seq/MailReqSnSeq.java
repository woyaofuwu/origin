package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class MailReqSnSeq extends SeqBase
{
    public MailReqSnSeq()
    {
        super("SEQ_MAIL_REQSN", 100);
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
